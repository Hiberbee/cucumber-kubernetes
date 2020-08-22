package dev.hiberbee.features;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.*;
import dev.hiberbee.dsl.Maybe;
import io.cucumber.java.*;
import io.cucumber.java.en.*;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import org.assertj.core.api.Assertions;
import org.junit.platform.commons.function.Try;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.*;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.function.Supplier;

@CacheConfig(cacheNames = "cucumber")
public class KubernetesFeature {

  private final KubernetesClient kubernetesClient;
  private final ObjectMapper objectMapper;
  private final Converter<String, String> dslNameConverter;
  private final Cache cache;

  public KubernetesFeature(
      @Autowired final Converter<String, String> dslNameConverter,
      @Autowired final ObjectMapper objectMapper,
      @Autowired final KubernetesClient kubernetesClient,
      @Value("#{cacheManager.getCache('cucumber')}") final Cache cache) {
    this.kubernetesClient = kubernetesClient;
    this.objectMapper = objectMapper;
    this.dslNameConverter = dslNameConverter;
    this.cache = cache;
  }

  @DefaultDataTableEntryTransformer
  @DefaultDataTableCellTransformer
  @DefaultParameterTransformer
  public Object defaultTransformer(final Object fromValue, final Type toValueType) {
    return this.objectMapper.convertValue(fromValue, this.objectMapper.constructType(toValueType));
  }

  /**
   * @param value One of "is" | "is not" | "are" | "are not" | "has" | "has not" | "should" |
   *     "should not" . Case insensitive
   * @return {@link Maybe}
   */
  @ParameterType("(is|is not|has|has not|have|have not|should|should not|are|are not|contains)")
  public Maybe maybe(final String value) {
    return Enums.stringConverter(Maybe.class)
                .compose(this.dslNameConverter::convert)
                .apply(value);
  }

  @ParameterType(
      "(pods|services|ingresses|deployments|replica sets|daemon sets|stateful sets|secrets|config maps)")
  public Supplier<MixedOperation<?, ? extends KubernetesResourceList<?>, ?, ?>> kubernetesResource(
      @Nonnull final String value) {
    return switch (value) {
      case "services" -> this.kubernetesClient::services;
      case "ingresses" -> this.kubernetesClient.network()::ingress;
      case "config maps" -> this.kubernetesClient::configMaps;
      case "secrets" -> this.kubernetesClient::secrets;
      case "deployments" -> this.kubernetesClient.apps()::deployments;
      case "daemon sets" -> this.kubernetesClient.apps()::daemonSets;
      case "replica sets" -> this.kubernetesClient.apps()::replicaSets;
      default -> this.kubernetesClient::pods;
    };
  }

  @When("{string} added and installed")
  public void dependencyAddedAndInstalled(final String dependency) {
    Try.call(() -> Files.readAllBytes(Paths.get(System.getenv("HOME"), ".Brewfile.lock.json")))
       .ifSuccess(it -> Assertions.assertThat(new String(it)).contains(dependency))
       .ifFailure(it -> Assertions.fail(it.getMessage()));
  }

  @Given("{maybe} resource with {string} {maybe} equal to {string}")
  public void resourceWithPathMaybeExist(
      @Nonnull final Maybe maybeHas,
      @Nonnull final String path,
      @Nonnull final Maybe maybeEqualTo,
      @Nonnull final String value) {
    final var resources = this.cache.get("resources", ArrayList<HasMetadata>::new);
    Assertions.assertThat(resources).extracting(path).anyMatch(it -> it.toString().contains(value));
  }

  @Given("kubernetes master url {maybe} {string}")
  public void kubernetesIsRunningOn(@Nonnull final Maybe maybe, final String host) {
    Assertions.assertThat(this.kubernetesClient.getMasterUrl().toString().contains(host))
        .isEqualTo(maybe.yes());
  }

  @Given("namespace is {string}")
  @CachePut(key = "#root.methodName")
  public Namespace namespace(final String expected) {
    final var namespace = this.kubernetesClient.namespaces().withName(expected).get();
    Assertions.assertThat(namespace).isNotNull();
    return namespace;
  }

  @Given("context is {string}")
  public void context(final String expected) {
    final var context = new NamedContext();
    context.setName(expected);
    this.kubernetesClient.getConfiguration().setCurrentContext(context);
  }

  @Given("{string} command {maybe} executable")
  public void subPathExists(final String command, @Nonnull final Maybe maybe) {
    Try.call(() -> Runtime.getRuntime().exec(command)).ifFailure(e->Assertions.fail(e.getMessage()));
  }

  @When("I get {kubernetesResource}")
  @CachePut(key = "#root.methodName")
  public <T extends HasMetadata> List<T> resources(
      @Nonnull
          final Supplier<MixedOperation<T, KubernetesResourceList<T>, ?, ?>>
              kubernetesResourceSupplier) {
    final var namespace = this.cache.get("namespace", Namespace.class);
    final var operation = kubernetesResourceSupplier.get();
    return (namespace == null)
        ? operation.inAnyNamespace().list().getItems()
        : operation.inNamespace(namespace.getMetadata().getNamespace()).list().getItems();
  }

  @Then("list size {maybe} greater then {int}")
  public void listSize(final Maybe maybe, final Integer size) {
    Assertions.assertThat(this.cache.get("resources", List.class))
        .hasSizeGreaterThanOrEqualTo(size);
  }
}

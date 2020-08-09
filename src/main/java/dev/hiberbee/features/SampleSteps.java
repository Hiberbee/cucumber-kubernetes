/*
 * MIT License
 *
 * Copyright (c) 2020 Hiberbee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.hiberbee.features;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.*;

@CacheConfig(cacheNames = "cucumber")
public class SampleSteps {

  private final ObjectMapper objectMapper;

  @Value("#{cacheManager.getCache('cucumber')}")
  private Cache cache;

  public SampleSteps(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Given("simple step")
  public void stepExample() {
    Assertions.assertThat(true).isTrue();
  }

  @Given("step with parameter {string}")
  public void stepExampleWithParameter(final String parameter) {
    Assertions.assertThat(parameter).isNotEmpty();
  }

  @Given("step with autowired bean dependency")
  public void stepExampleWithBeanDependency() {
    Assertions.assertThat(this.objectMapper).isInstanceOf(ObjectMapper.class);
  }

  @Given("step returns {string} example")
  @CachePut(key = "#root.methodName")
  public String stepReturnsExample(final String value) {
    return value;
  }

  @Given("step result {string} assertion example")
  public void stepValuePersistenceExample(final String value) {
    Assertions.assertThat(this.cache.get("stepReturnsExample", String.class)).isEqualTo(value);
  }
}

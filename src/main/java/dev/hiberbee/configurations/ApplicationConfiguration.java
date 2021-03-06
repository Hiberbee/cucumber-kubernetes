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

package dev.hiberbee.configurations;

import com.google.common.base.Converter;
import dev.hiberbee.TestApplication;
import io.fabric8.kubernetes.client.*;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.*;

import javax.annotation.Nonnull;

@Configuration
@ComponentScan(basePackageClasses = TestApplication.class)
public class ApplicationConfiguration {

  @Bean
  public Converter<String, String> dslNameConverter() {
    return new Converter<>() {
      @Override
      protected String doForward(final @Nonnull String s) {
        return s.toUpperCase().replace(' ', '_');
      }

      @Override
      protected String doBackward(final @Nonnull String s) {
        return s.toLowerCase().replace('_', ' ');
      }
    };
  }

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager("cucumber");
  }

  @Bean
  public KubernetesClient kubernetesClient() {
    return new DefaultKubernetesClient();
  }
}

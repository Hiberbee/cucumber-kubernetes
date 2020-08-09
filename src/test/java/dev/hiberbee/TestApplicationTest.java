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

package dev.hiberbee;

import dev.hiberbee.configurations.ApplicationConfiguration;
import io.cucumber.spring.CucumberContextConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.*;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.*;

@EnableCaching
@SpringBootTest(
    classes = ApplicationConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@CucumberContextConfiguration
class TestApplicationTest {

  @Autowired private CacheManager cacheManager;

  @Value("#{cacheManager.getCache('cucumber')}")
  private Cache cache;

  @Test
  void testCache() {
    Assertions.assertThat(this.cacheManager).isInstanceOf(ConcurrentMapCacheManager.class);
    Assertions.assertThat(this.cache).isInstanceOf(ConcurrentMapCache.class);
  }
}

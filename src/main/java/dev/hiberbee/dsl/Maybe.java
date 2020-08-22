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

package dev.hiberbee.dsl;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Predicate;

public enum Maybe {
  CONTAINS,
  SHOULD_BE,
  SHOULD_NOT_BE,
  IS_NOT,
  IS,
  HAS_NOT,
  HAS,
  HAVE,
  HAVE_NOT,
  ARE,
  ARE_NOT;

  public @Nonnull Predicate<Boolean> predicate() {
    return Predicate.isEqual(
        Predicate.isEqual(IS)
            .or(Predicate.isEqual(ARE))
            .or(Predicate.isEqual(SHOULD_BE))
            .or(Predicate.isEqual(HAS))
            .or(Predicate.isEqual(HAVE))
            .or(Predicate.isEqual(CONTAINS))
            .test(this));
  }

  public @Nonnull Boolean plural() {
    return this.equals(ARE) || this.equals(HAVE);
  }

  public @Nonnull Boolean yes() {
    return this.predicate().test(true);
  }

  public @Nonnull Boolean no() {
    return this.predicate().test(false);
  }

  public @Nonnull Optional<Boolean> optional() {
    return Optional.of(this.yes());
  }
}

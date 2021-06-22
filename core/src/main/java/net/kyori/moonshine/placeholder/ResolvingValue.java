//
// moonshine - A localisation library for Java.
// Copyright (C) 2021 Mariell Hoversholm
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published
// by the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

package net.kyori.moonshine.placeholder;

import javax.annotation.concurrent.ThreadSafe;
import org.checkerframework.dataflow.qual.Pure;

/**
 * A value component of a resolving result.
 *
 * @param <F> the value to store
 */
@ThreadSafe
public abstract class ResolvingValue<F> {
  private final F value;

  /* package-private */ ResolvingValue(final F value) {
    this.value = value;
  }

  /**
   * @return the value for the resolving result
   */
  @Pure
  public F value() {
    return this.value;
  }
}

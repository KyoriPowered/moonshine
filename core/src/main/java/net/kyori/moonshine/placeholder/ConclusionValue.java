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

import net.kyori.moonshine.annotation.meta.ThreadSafe;
import org.checkerframework.dataflow.qual.SideEffectFree;

/**
 * A now fully concluded value, without necessarily being a concluded type.
 *
 * @param <F> the type of the value
 */
@ThreadSafe
public final class ConclusionValue<F> extends ResolvingValue<F> {
  private ConclusionValue(final F value) {
    super(value);
  }

  @SideEffectFree
  public static <F> ConclusionValue<F> conclusionValue(final F value) {
    return new ConclusionValue<>(value);
  }
}

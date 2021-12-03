/*
 * moonshine - A localisation library for Java.
 * Copyright (C) Mariell Hoversholm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.kyori.moonshine.util;

import net.kyori.moonshine.annotation.meta.ThreadSafe;

/**
 * A weighted value.
 *
 * @param <V> the value contained
 */
@ThreadSafe
public final record Weighted<V>(V value, int weight) implements Comparable<Weighted<V>> {
  @Override
  public int compareTo(final Weighted<V> o) {
    return Integer.compare(this.weight(), o.weight());
  }
}

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

package net.kyori.moonshine.util;

import java.util.Objects;
import net.kyori.moonshine.annotation.meta.ThreadSafe;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

/**
 * A weighted value.
 *
 * @param <V> the value contained
 */
@ThreadSafe
public final class Weighted<V> implements Comparable<Weighted<V>> {
  private final V value;
  private final int weight;

  public Weighted(final V value, final int weight) {
    this.value = value;
    this.weight = weight;
  }

  @Pure
  public V value() {
    return this.value;
  }

  @Pure
  public int weight() {
    return this.weight;
  }

  @Override
  public int compareTo(final Weighted<V> o) {
    return Integer.compare(this.weight(), o.weight());
  }

  @Override
  public boolean equals(final @Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final Weighted<?> weighted = (Weighted<?>) o;
    return this.weight() == weighted.weight() && this.value().equals(weighted.value());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value(), this.weight());
  }

  @Override
  public String toString() {
    return "Weighted{value=" + this.value() + ", weight=" + this.weight() + '}';
  }
}

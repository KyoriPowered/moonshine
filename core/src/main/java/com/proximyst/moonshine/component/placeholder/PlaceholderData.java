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

package com.proximyst.moonshine.component.placeholder;

import com.proximyst.moonshine.annotation.Placeholder;
import com.proximyst.moonshine.proxy.MessageMethod;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * Data about a single placeholder on a {@link MessageMethod}.
 */
public final class PlaceholderData {
  private final String name;
  private final AnnotatedType type;
  private final String[] flags;
  private final int index;

  public PlaceholderData(final String name, final AnnotatedType type, final String[] flags, final int index) {
    this.name = name;
    this.type = type;
    this.flags = flags;
    this.index = index;
  }

  /**
   * @return The name of the placeholder, as provided by {@link Placeholder} or the placeholder name itself.
   */
  public String name() {
    return this.name;
  }

  /**
   * @return The type of the placeholder, as close as possible to the actual code.
   */
  public AnnotatedType type() {
    return this.type;
  }

  /**
   * @return The flags this placeholder can accept.
   */
  public String[] flags() {
    return this.flags;
  }

  /**
   * @return The index this placeholder is located at in the parameters of the {@link Method}.
   */
  public int index() {
    return this.index;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final PlaceholderData that = (PlaceholderData) o;
    return this.name.equals(that.name()) &&
        this.type.equals(that.type()) &&
        Arrays.equals(this.flags(), that.flags()) &&
        this.index() == that.index();
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(this.name(), this.type());
    result = 31 * result + Arrays.hashCode(this.flags());
    result = 31 * result + Integer.hashCode(this.index());
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Placeholder{");
    sb.append("name='").append(this.name()).append('\'');
    sb.append(", type=").append(this.type());
    sb.append(", flags=").append(Arrays.toString(this.flags()));
    sb.append(", index=").append(this.index());
    sb.append('}');
    return sb.toString();
  }
}

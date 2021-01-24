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

import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.Objects;

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

  public String name() {
    return this.name;
  }

  public AnnotatedType type() {
    return this.type;
  }

  public String[] flags() {
    return this.flags;
  }

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

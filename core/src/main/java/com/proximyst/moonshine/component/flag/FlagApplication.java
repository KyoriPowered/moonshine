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

package com.proximyst.moonshine.component.flag;

import java.util.Objects;

public final class FlagApplication {
  private final Class<?> type;
  private final String name;

  public FlagApplication(final Class<?> type, final String name) {
    this.type = type;
    this.name = name;
  }

  public Class<?> type() {
    return this.type;
  }

  public String name() {
    return this.name;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final FlagApplication that = (FlagApplication) o;
    return this.type().equals(that.type()) &&
        this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.type(), this.name());
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("FlagApplication{");
    sb.append("type=").append(this.type());
    sb.append(", name='").append(this.name()).append('\'');
    sb.append('}');
    return sb.toString();
  }
}

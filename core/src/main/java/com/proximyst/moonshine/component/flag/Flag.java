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
import java.util.Set;

public final class Flag<T> {
  private final Set<FlagApplication> flagApplications;
  private final Class<?> type;
  private final int parameterIdx;

  public Flag(final Set<FlagApplication> flagApplications, final Class<?> type, final int parameterIdx) {
    this.flagApplications = flagApplications;
    this.type = type;
    this.parameterIdx = parameterIdx;
  }

  public Set<FlagApplication> flagApplications() {
    return this.flagApplications;
  }

  public Class<?> type() {
    return this.type;
  }

  public int parameterIdx() {
    return this.parameterIdx;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final Flag<?> flag = (Flag<?>) o;
    return this.parameterIdx() == flag.parameterIdx() &&
        this.flagApplications().equals(flag.flagApplications()) &&
        this.type().equals(flag.type());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.flagApplications(), this.type(), this.parameterIdx());
  }
}

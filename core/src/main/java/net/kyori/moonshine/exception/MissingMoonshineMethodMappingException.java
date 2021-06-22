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

package net.kyori.moonshine.exception;

import net.kyori.moonshine.internal.ReflectiveUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class MissingMoonshineMethodMappingException extends MoonshineException {
  private final Type owner;
  private final Method method;

  public MissingMoonshineMethodMappingException(final Type owner, final Method method) {
    super("A method was not mapped by Moonshine: " + ReflectiveUtils.formatMethodName(owner, method));
    this.owner = owner;
    this.method = method;
  }

  public Type owner() {
    return this.owner;
  }

  public Method method() {
    return this.method;
  }
}

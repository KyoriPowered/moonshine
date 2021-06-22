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

package net.kyori.moonshine.exception.scan;

import net.kyori.moonshine.Moonshine;
import net.kyori.moonshine.internal.ReflectiveUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * A method was declared in the interface to return {@link Moonshine}, but is not a legal definition.
 */
public final class IllegalReturningMoonshineMethodException extends UnscannableMethodException {
  public IllegalReturningMoonshineMethodException(final Type owner, final Method method) {
    super(owner, method,
        "Non-default methods that return Moonshine must have 0 parameters, yet following method does not: "
            + ReflectiveUtils.formatMethodName(owner, method));
  }
}

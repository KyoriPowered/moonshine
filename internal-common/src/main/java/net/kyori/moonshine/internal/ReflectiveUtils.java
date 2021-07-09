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

package net.kyori.moonshine.internal;

import io.leangen.geantyref.GenericTypeReflector;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Utilities for handling reflective operations.
 */
public final class ReflectiveUtils {
  private ReflectiveUtils() {
  }

  /**
   * Formats a method name in the following style: {@code a.b.c.Owner<T>#methodName(T, String,
   * int)a.b.c.ReturnType<R>}.
   *
   * @param owner  the owner/declaring type of this method
   * @param method the method to format
   * @return the formatted method descriptor
   */
  public static String formatMethodName(final Type owner, final Method method) {
    return GenericTypeReflector.getTypeName(owner)
        + '#'
        + method.getName()
        + formatMethodTypeParameters(method)
        + '('
        + formatMethodParameters(method)
        + ')'
        + GenericTypeReflector.getTypeName(method.getGenericReturnType());
  }

  private static String formatMethodParameters(final Method method) {
    if (method.getParameterCount() == 0) {
      return "";
    }

    return Arrays.stream(method.getGenericParameterTypes())
        .map(GenericTypeReflector::getTypeName)
        .collect(Collectors.joining(", "));
  }

  private static String formatMethodTypeParameters(final Method method) {
    final TypeVariable<Method>[] typeParameters = method.getTypeParameters();
    if (typeParameters.length == 0) {
      return "";
    }

    return Arrays.stream(typeParameters)
        .map(GenericTypeReflector::getTypeName)
        .collect(Collectors.joining(", ", "<", ">"));
  }
}

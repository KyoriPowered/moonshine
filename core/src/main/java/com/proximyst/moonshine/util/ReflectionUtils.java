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

package com.proximyst.moonshine.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class ReflectionUtils {
  private ReflectionUtils() {
  }

  /**
   * Formats a method to a human-readable and machine-readable string.
   *
   * @param method The method to format.
   * @return The method in a string of {@code declaring.package.ClassName#methodName(int,String,ParameterType<Generic>)ReturnType<Generic>}.
   */
  public static String formatMethod(final Method method) {
    final String declaring = method.getDeclaringClass().getName();
    final String name = method.getName();
    final String returningType = method.getGenericReturnType().getTypeName();
    final String parameters = Arrays.stream(method.getGenericParameterTypes())
        .map(Type::getTypeName)
        .collect(Collectors.joining(","));

    return declaring + "#" + name + "(" + parameters + ")" + returningType;
  }

  public static boolean isJava9() {
    try {
      Optional.class.getDeclaredMethod("ifPresentOrElse", Consumer.class, Runnable.class);
      return true;
    } catch (final NoSuchMethodException ignored) {
      return false;
    }
  }

  public static boolean isEqualsMethod(final Method method) {
    return method.getName().equals("equals")
        && method.getParameterCount() == 1
        && method.getReturnType() == Boolean.TYPE;
  }

  public static boolean isHashCodeMethod(final Method method) {
    return method.getName().equals("hashCode")
        && method.getParameterCount() == 0
        && method.getReturnType() == Integer.TYPE;
  }

  public static boolean isToStringMethod(final Method method) {
    return method.getName().equals("toString")
        && method.getParameterCount() == 0
        && method.getReturnType() == String.class;
  }

  public static boolean canThrow(final Method method, final Class<? extends Throwable> type) {
    for (final Class<?> declared : method.getExceptionTypes()) {
      if (declared == type || declared.isAssignableFrom(type)) {
        return true;
      }
    }

    return false;
  }
}

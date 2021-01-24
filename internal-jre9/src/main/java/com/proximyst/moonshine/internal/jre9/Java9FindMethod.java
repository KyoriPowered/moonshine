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

package com.proximyst.moonshine.internal.jre9;

import com.proximyst.moonshine.internal.IFindMethod;
import com.proximyst.moonshine.internal.ThrowableUtils;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class Java9FindMethod implements IFindMethod {
  private final Map<Method, MethodHandle> methodCache = new HashMap<>();

  @Override
  public MethodHandle findMethod(final Method method, final Object proxy) {
    final Class<?> type = method.getDeclaringClass();

    return this.methodCache.computeIfAbsent(method,
        methodParam -> {
          try {
            return MethodHandles.lookup()
                .findSpecial(type,
                    method.getName(),
                    MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                    type)
                .bindTo(proxy);
          } catch (final NoSuchMethodException | IllegalAccessException ex) {
            ThrowableUtils.sneakyThrow(ex);
            throw new RuntimeException(ex);
          }
        });
  }
}

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

package com.proximyst.moonshine.internal.jre8;

import com.proximyst.moonshine.internal.IFindMethod;
import com.proximyst.moonshine.internal.ThrowableUtils;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class Java8FindMethod implements IFindMethod {
  private final Map<Method, MethodHandle> methodCache = new HashMap<>();
  private final Constructor<Lookup> lookupConstructor;

  public Java8FindMethod() {
    try {
      this.lookupConstructor = Lookup.class.getDeclaredConstructor(Class.class);
      this.lookupConstructor.setAccessible(true);
    } catch (final NoSuchMethodException ex) {
      ThrowableUtils.sneakyThrow(ex);
      throw new RuntimeException(ex);
    }
  }

  @Override
  public MethodHandle findMethod(final Method method, final Object proxy) {
    final Class<?> type = method.getDeclaringClass();

    return this.methodCache.computeIfAbsent(method,
        methodParam -> {
          try {
            return this.lookupConstructor.newInstance(type)
                .in(type)
                .unreflectSpecial(method, type)
                .bindTo(proxy);
          } catch (final ReflectiveOperationException ex) {
            ThrowableUtils.sneakyThrow(ex);
            throw new RuntimeException(ex);
          }
        });
  }
}

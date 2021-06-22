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

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * Utility to find methods.
 */
public interface IFindMethod {
  /**
   * Find a single {@code default} method for the given interfaces of a {@link java.lang.reflect.Proxy proxy}.
   *
   * @param method the method to find
   * @param proxy  the proxy to find the method within
   * @return the found method
   * @throws IllegalAccessException if the method is inaccessible
   */
  MethodHandle findMethod(final Method method, final Object proxy) throws IllegalAccessException;
}

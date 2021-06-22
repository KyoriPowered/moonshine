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

package net.kyori.moonshine.strategy.supertype;

import java.lang.reflect.Type;
import java.util.Iterator;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A strategy to select supertypes of a given type.
 */
@FunctionalInterface
@ThreadSafe
public interface ISupertypeStrategy {
  /**
   * Creates a new iterator that iterates the hierarchy of a type with the current strategy.
   *
   * @param type the type to iterate the hierarchy of
   * @return the hierarchy type iterator
   */
  Iterator<Type> hierarchyIterator(final Type type);
}

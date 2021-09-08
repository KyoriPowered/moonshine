/*
 * moonshine - A localisation library for Java.
 * Copyright (C) Mariell Hoversholm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.kyori.moonshine.strategy.supertype;

import io.leangen.geantyref.GenericTypeReflector;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;
import net.kyori.moonshine.annotation.meta.ThreadSafe;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A strategy for selecting the supertype of a class, then its interfaces, then their supertypes before moving on to the
 * supertypes' interfaces and so forth.
 * <p>
 * For the given definition:
 * <br><code>
 * interface SuperSuper, SuperA1 extends SuperSuper, SuperA2, SuperA3, SuperB1, SuperB2, SuperB3
 * <br> interface SuperA extends SuperA1, SuperA3, SuperA2
 * <br> interface SuperB extends SuperB1, SuperB3, SuperB2
 * <br> class A implements SuperA, SuperB2
 * <br> class B extends A implements SuperB
 * </code>
 * <br>... the following hierarchy will be iterated:
 * <ol>
 *   <li>{@code A}</li>
 *   <li>{@code B}</li>
 *   <li>{@code Object} -- only if {@link #returnObject} is true</li>
 *   <li>{@code SuperA}</li>
 *   <li>{@code SuperB2}</li>
 *   <li>{@code SuperA1}</li>
 *   <li>{@code SuperA3} -- This is done therefore per declaration, or whichever order the JVM returns the supertypes in</li>
 *   <li>{@code SuperA2}</li>
 *   <li>{@code SuperSuper} -- This is the supertype of {@code SuperA1}, and is therefore more important than the supertypes of
 *   {@code B}'s implemented interfaces</li>
 *   <li>{@code SuperB}</li>
 *   <li>{@code SuperB1}</li>
 *   <li>{@code SuperB3}</li>
 *   <li>No {@code SuperB2}, because {@code A} implements this</li>
 * </ol>
 */
@ThreadSafe
public final class StandardSupertypeThenInterfaceSupertypeStrategy implements ISupertypeStrategy {
  /**
   * A simple caching map to avoid locks altogether with thread-safety at the cost of potentially having concurrently
   * computing calls of the same hierarchy.
   */
  private final ConcurrentMap<Type, LinkedHashSet<Type>> typeToHierarchyCache = new ConcurrentHashMap<>();

  /**
   * Whether this should return {@link Object}, assuming this is not an {@link Object} already.
   */
  private final boolean returnObject;

  public StandardSupertypeThenInterfaceSupertypeStrategy(final boolean returnObject) {
    this.returnObject = returnObject;
  }

  @Override
  public Iterator<Type> hierarchyIterator(final Type type) {
    final Class<?> erasedBaseType = GenericTypeReflector.erase(type);
    if (erasedBaseType == Object.class) {
      final Set<Type> types = Collections.singleton(Object.class);
      return types.iterator();
    }

    return this.typeToHierarchyCache.computeIfAbsent(type, key -> {
      final LinkedHashSet<Type> aggregatedSuperTypes = this.aggregateSuperTypes(type);
      final LinkedHashSet<Type> aggregatedSuperInterfaces =
          this.aggregateSuperInterfaces(Stream.concat(Stream.of(type), aggregatedSuperTypes.stream()), type);
      final LinkedHashSet<Type> conjoined = new LinkedHashSet<>(
          aggregatedSuperTypes.size() + aggregatedSuperInterfaces.size());
      conjoined.addAll(aggregatedSuperTypes);
      conjoined.addAll(aggregatedSuperInterfaces);

      return conjoined;
    }).iterator();
  }

  private LinkedHashSet<Type> aggregateSuperTypes(final Type baseType) {
    final LinkedHashSet<Type> types = new LinkedHashSet<>();
    @Nullable Class<?> erasedSuperType = GenericTypeReflector.erase(baseType);
    while ((erasedSuperType = erasedSuperType.getSuperclass()) != null
        && (this.returnObject || erasedSuperType != Object.class)) {
      types.add(GenericTypeReflector.getExactSuperType(baseType, erasedSuperType));
    }

    return types;
  }

  private LinkedHashSet<Type> aggregateSuperInterfaces(final Stream<Type> aggregatedSuperTypes,
      final Type baseType) {
    final LinkedHashSet<Type> types = new LinkedHashSet<>();

    final Iterator<Type> superTypes = aggregatedSuperTypes.iterator();
    while (superTypes.hasNext()) {
      final Type superType = superTypes.next();
      final Class<?>[] interfaces = GenericTypeReflector.erase(superType).getInterfaces();
      for (final Class<?> iface : interfaces) {
        final @Nullable Type exact = GenericTypeReflector.getExactSuperType(baseType, iface);
        if (exact == null) {
          types.add(iface);
        } else {
          types.add(exact);
        }
      }
      for (final Class<?> iface : interfaces) {
        types.addAll(this.aggregateSuperInterfaces(Stream.of(iface), baseType));
      }
    }

    return types;
  }
}

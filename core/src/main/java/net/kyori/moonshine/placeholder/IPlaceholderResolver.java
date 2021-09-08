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
package net.kyori.moonshine.placeholder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import net.kyori.moonshine.annotation.meta.ThreadSafe;
import net.kyori.moonshine.util.Either;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A resolver for a placeholder of type {@link P}.
 *
 * @param <R> the receiver type
 * @param <P> the input placeholder type, or a supertype thereof
 * @param <F> the finalised placeholder type
 */
@FunctionalInterface
@ThreadSafe
public interface IPlaceholderResolver<R, P, F> {
  /**
   * Resolves a given value into a result. In most cases, you want to return {@link
   * ContinuanceValue#continuanceValue(Object, Type) ContinuanceValue}s.
   *
   * @param placeholderName the name of the placeholder that is currently being resolved; two results cannot share name,
   *                        so this is only applicable as a prefix or for the map keys
   * @param value           the value of the input placeholder, of type {@link P}
   * @param receiver        the eventual receiver of the message
   * @param owner           the owner of the method
   * @param method          the method called
   * @param parameters      the parameters passed to the method
   * @return the resolved placeholder(s), or {@code null} if you wish to pass on the resolving to the next resolver. The
   * map must be {@code { placeholder name => state value }}
   */
  @Nullable Map<String, Either<ConclusionValue<? extends F>, ContinuanceValue<?>>> resolve(
      final String placeholderName, final P value, final R receiver, final Type owner,
      final Method method, final @Nullable Object[] parameters);
}

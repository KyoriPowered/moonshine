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

package com.proximyst.moonshine.component.placeholder;

import com.google.common.collect.Multimap;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A resolver for a placeholder of type {@code V}.
 * <p>
 * This must resolve to a {@link ResolveResult}.
 *
 * @param <R> The receiver type.
 * @param <V> The value type to resolve into a result.
 */
public interface IPlaceholderResolver<R, V> {
  /**
   * Resolve the given value into a result to use in a message, or for further resolving. In most cases, you want to
   * return {@link ResolveResult#ok(Object)} or {@link ResolveResult#pass()}.
   *
   * @param value The value to resolve with. This is never {@code null}.
   * @param ctx   The context for this resolver.
   * @param flags The flags provided to the resolver.
   * @return The {@link ResolveResult} for this resolving.
   */
  ResolveResult resolve(final V value, final PlaceholderContext<R> ctx,
      final Multimap<String, @Nullable Object> flags);
}

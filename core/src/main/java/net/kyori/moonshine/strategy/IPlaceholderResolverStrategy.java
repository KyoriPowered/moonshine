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

package net.kyori.moonshine.strategy;

import net.kyori.moonshine.Moonshine;
import net.kyori.moonshine.exception.PlaceholderResolvingException;
import net.kyori.moonshine.model.MoonshineMethod;
import java.util.Map;
import javax.annotation.concurrent.ThreadSafe;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A strategy for resolving placeholders in a message.
 */
@FunctionalInterface
@ThreadSafe
public interface IPlaceholderResolverStrategy<R, I, F> {
  /**
   * Resolve all placeholders with the current strategy.
   *
   * @param moonshine        the {@link Moonshine} meta instance for resolvers
   * @param intermediateText the invocation's intermediate text; can be useful to determine if any resolving should be
   *                         done at all, should text be missing
   * @param moonshineMethod  the scanned method that was invoked
   * @param parameters       the parameters in the invocation to this method
   * @return a map of all resolved placeholders
   */
  Map<String, ? extends F> resolvePlaceholders(final Moonshine<R, I, ?, F> moonshine, final R receiver,
      final I intermediateText, final MoonshineMethod<? extends R> moonshineMethod,
      final @Nullable Object[] parameters)
      throws PlaceholderResolvingException;
}

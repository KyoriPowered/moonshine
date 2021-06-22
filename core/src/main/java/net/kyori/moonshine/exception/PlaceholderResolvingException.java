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

package net.kyori.moonshine.exception;

import net.kyori.moonshine.strategy.IPlaceholderResolverStrategy;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An exceptional case occurred during resolving of placeholders with a {@link IPlaceholderResolverStrategy}.
 */
public abstract class PlaceholderResolvingException extends MoonshineException {
  protected PlaceholderResolvingException() {
  }

  protected PlaceholderResolvingException(final @Nullable String message) {
    super(message);
  }

  protected PlaceholderResolvingException(final @Nullable String message,
      final @Nullable Throwable cause) {
    super(message, cause);
  }

  protected PlaceholderResolvingException(final @Nullable Throwable cause) {
    super(cause);
  }

  protected PlaceholderResolvingException(final @Nullable String message,
      final @Nullable Throwable cause, final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

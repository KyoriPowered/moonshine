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

import net.kyori.moonshine.receiver.IReceiverLocator;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A {@link IReceiverLocator receiver locator} could not locate a receiver for a given method invocation.
 */
public abstract class ReceiverMissingException extends MoonshineException {
  protected ReceiverMissingException() {
  }

  protected ReceiverMissingException(final @Nullable String message) {
    super(message);
  }

  protected ReceiverMissingException(final @Nullable String message,
      final @Nullable Throwable cause) {
    super(message, cause);
  }

  protected ReceiverMissingException(final @Nullable Throwable cause) {
    super(cause);
  }

  protected ReceiverMissingException(final @Nullable String message, final @Nullable Throwable cause,
      final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

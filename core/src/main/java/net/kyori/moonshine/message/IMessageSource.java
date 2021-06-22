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

package net.kyori.moonshine.message;

import net.kyori.moonshine.exception.MissingMessageException;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A source for messages.
 *
 * @param <R> the receiver type
 * @param <I> the intermediate message type
 */
@FunctionalInterface
@ThreadSafe
public interface IMessageSource<R, I> {
  /**
   * Source a message of a key with the given {@code receiver}.
   *
   * @param receiver   the eventual receiver of this message
   * @param messageKey the key of this message
   * @return the message found
   * @throws MissingMessageException if there is no message for this key found, and a thrown exception is preferred over
   *                                 a generic message
   */
  I messageOf(final R receiver, final String messageKey) throws MissingMessageException;
}

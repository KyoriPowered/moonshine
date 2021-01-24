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

package com.proximyst.moonshine.message;

public interface IMessageSource<T> {
  /**
   * Fetch a message from the source of messages.
   * <p>
   * There are no implementation detail guarantees on this. It may use any kind of underlying types the implementer
   * wants.
   * </p>
   * <p>
   * <b>Note:</b> This should support keys with {@code .}s (full-stops) in them, as this is the standard of all message
   * keys.
   * <br>
   * <b>Note:</b> This must never fail. There must always be a message for a given key, even if this message is just an
   * unimplemented warning.
   * </p>
   *
   * @return the message from the source.
   */
  T message(final String key);
}

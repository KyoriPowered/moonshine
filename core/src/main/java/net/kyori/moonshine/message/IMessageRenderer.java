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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A rendered of intermediate messages with resolved placeholders for a given receiver.
 *
 * @param <R> the eventual receiver type of this message
 * @param <I> the intermediate message type
 * @param <O> the output/rendered message type
 * @param <F> the finalised placeholder type
 */
@FunctionalInterface
@ThreadSafe
public interface IMessageRenderer<R, I, O, F> {
  /**
   * Render the intermediate message into a rendered message.
   * <p>
   * <b>Note:</b> This must be infallible, meaning any errors should be done earlier on.
   * </p>
   *
   * @param receiver             the receiver of the message
   * @param intermediateMessage  the intermediate message to render
   * @param resolvedPlaceholders the resolved placeholders of this message
   * @param method               the method invoked
   * @param owner                the owner of the method
   * @return the rendered message
   */
  O render(final R receiver, final I intermediateMessage, final Map<String, ? extends F> resolvedPlaceholders,
      final Method method, final Type owner);
}

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
package net.kyori.moonshine.message;

import net.kyori.moonshine.annotation.meta.ThreadSafe;

/**
 * A trait that defines how to send finalised messages to the given receiver.
 *
 * @param <R> the receiver type of the message
 * @param <O> the output/rendered message
 */
@FunctionalInterface
@ThreadSafe
public interface IMessageSender<R, O> {
  /**
   * Send the message to the given receiver.
   *
   * @param receiver        the receiver of the message
   * @param renderedMessage the rendered message to send to the receiver
   */
  void send(R receiver, O renderedMessage);
}

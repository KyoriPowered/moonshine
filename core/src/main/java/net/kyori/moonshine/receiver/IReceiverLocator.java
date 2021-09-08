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
package net.kyori.moonshine.receiver;

import java.lang.reflect.Method;
import net.kyori.moonshine.annotation.meta.ThreadSafe;
import net.kyori.moonshine.exception.ReceiverMissingException;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A protocol to locate a receiver on a method with given parameters. Must be infallible or throw a fitting exception.
 *
 * @param <R> the receiver type to locate
 */
@FunctionalInterface
@ThreadSafe
public interface IReceiverLocator<R> {
  /**
   * Locate a receiver on a given proxy invocation.
   * <p>
   * If no receiver is wanted, a "unit receiver" should be created in its stead; this means a generic receiver that will
   * simply no-op on any messages sent.
   * </p>
   *
   * @param method     the method invoked
   * @param proxy      the proxy {@code method} was invoked on
   * @param parameters the parameters passed to {@code method} on invocation; the parameters may be {@code null}, but
   *                   the array itself is always non-{@code null}
   * @return the located receiver
   * @throws ReceiverMissingException if a receiver cannot be located
   */
  R locate(final Method method, final Object proxy, final @Nullable Object[] parameters)
      throws ReceiverMissingException;
}

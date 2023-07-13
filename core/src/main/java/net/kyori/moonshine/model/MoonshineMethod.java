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
package net.kyori.moonshine.model;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import net.kyori.moonshine.Moonshine;
import net.kyori.moonshine.MoonshineChild;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.MessageSection;
import net.kyori.moonshine.annotation.meta.ThreadSafe;
import net.kyori.moonshine.exception.scan.MissingMessageAnnotationException;
import net.kyori.moonshine.exception.scan.NoReceiverLocatorFoundException;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.receiver.IReceiverLocator;
import net.kyori.moonshine.receiver.IReceiverLocatorResolver;
import net.kyori.moonshine.util.Weighted;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

/**
 * A data class for a scanned method.
 *
 * @param <R> the eventual receiver type of this message
 */
@ThreadSafe
public final class MoonshineMethod<R> {
  private final Type owner;
  private final Method reflectMethod;
  private final @Nullable Object messageSectionProxy;
  private final String messageKey;

  private final @Nullable IReceiverLocator<? extends R> receiverLocator;

  public MoonshineMethod(
          final Moonshine<R, ?, ?, ?> moonshine,
          final Type owner,
          final ClassLoader proxyClassLoader,
          final Method reflectMethod,
          final String sectionFullKey,
          final char delimiter)
      throws UnscannableMethodException {
    this.owner = owner;
    this.reflectMethod = reflectMethod;

    final boolean isMessageSection = this.reflectMethod.getReturnType().isAnnotationPresent(MessageSection.class);

    this.messageKey = this.findMessageKey(sectionFullKey, delimiter, isMessageSection);
    // todo is IReceiverLocatorResolver needed?
    this.receiverLocator = isMessageSection ? null : this.findReceiverLocator(moonshine);

    Object messageSectionProxy = null;
    if (isMessageSection) {
      messageSectionProxy = new MoonshineChild<>(
              this.reflectMethod.getGenericReturnType(),
              proxyClassLoader,
              moonshine,
              this.messageKey
      ).proxy();
    }
    this.messageSectionProxy = messageSectionProxy;
  }

  /**
   * Returns the owning/declaring type of this method.
   */
  @Pure
  public Type owner() {
    return this.owner;
  }

  /**
   * Returns the {@link Method reflected method} of this method.
   */
  @Pure
  public Method reflectMethod() {
    return this.reflectMethod;
  }

  /**
   * Returns the message section proxy if this method is a message section, otherwise null.
   */
  public @Nullable Object messageSectionProxy() {
    return this.messageSectionProxy;
  }

  /**
   * The full key of the message. So this includes the parent key parts if it has any.
   */
  @Pure
  public String messageKey() {
    return this.messageKey;
  }

  /**
   * The locator for a given receiver of this message.
   */
  @Pure
  public @Nullable IReceiverLocator<? extends R> receiverLocator() {
    return this.receiverLocator;
  }

  private String findMessageKey(final String sectionFullKey, final char delimiter, final boolean isMessageSection) throws MissingMessageAnnotationException {
    final @Nullable Message annotation = this.reflectMethod.getAnnotation(Message.class);

    //noinspection ConstantConditions -- this is completely not true. It may be null, per its Javadocs.
    if (annotation == null) {
      if (isMessageSection) {
        return sectionFullKey;
      }
      throw new MissingMessageAnnotationException(this.owner, this.reflectMethod);
    }

    if (isMessageSection) {
      return sectionFullKey + annotation.value() + delimiter;
    }
    return sectionFullKey + annotation.value();
  }

  private IReceiverLocator<? extends R> findReceiverLocator(final Moonshine<R, ?, ?, ?> moonshine)
      throws NoReceiverLocatorFoundException {
    final Iterator<Weighted<? extends IReceiverLocatorResolver<? extends R>>> receiverLocatorResolverIterator =
        moonshine.weightedReceiverLocatorResolvers().descendingIterator();

    while (receiverLocatorResolverIterator.hasNext()) {
      final IReceiverLocatorResolver<? extends R> receiverLocatorResolver =
          receiverLocatorResolverIterator.next().value();
      final @Nullable IReceiverLocator<? extends R> resolvedLocator =
          receiverLocatorResolver.resolve(this.reflectMethod, this.owner);

      if (resolvedLocator != null) {
        return resolvedLocator;
      }
    }

    throw new NoReceiverLocatorFoundException(this.owner, this.reflectMethod);
  }
}

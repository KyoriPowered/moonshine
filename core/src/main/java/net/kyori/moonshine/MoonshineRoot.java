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
package net.kyori.moonshine;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableSet;
import net.kyori.moonshine.annotation.meta.ThreadSafe;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.message.IMessageRenderer;
import net.kyori.moonshine.message.IMessageSender;
import net.kyori.moonshine.message.IMessageSource;
import net.kyori.moonshine.placeholder.IPlaceholderResolver;
import net.kyori.moonshine.receiver.IReceiverLocatorResolver;
import net.kyori.moonshine.strategy.IPlaceholderResolverStrategy;
import net.kyori.moonshine.util.Weighted;

@ThreadSafe
final class MoonshineRoot<R, I, O, F> extends Moonshine<R, I, O, F> {
  private final IPlaceholderResolverStrategy<R, I, F> placeholderResolverStrategy;
  private final IMessageSource<R, I> messageSource;
  private final IMessageRenderer<R, I, O, F> messageRenderer;
  private final IMessageSender<R, O> messageSender;
  private final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers;
  private final Map<Type, NavigableSet<Weighted<? extends IPlaceholderResolver<? extends R, ?, ? extends F>>>> weightedPlaceholderResolvers;

  MoonshineRoot(
      final Type proxiedType,
      final ClassLoader classLoader,
      final IPlaceholderResolverStrategy<R, I, F> placeholderResolverStrategy,
      final IMessageSource<R, I> messageSource,
      final IMessageRenderer<R, I, O, F> messageRenderer,
      final IMessageSender<R, O> messageSender,
      final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers,
      final Map<Type, NavigableSet<Weighted<? extends IPlaceholderResolver<? extends R, ?, ? extends F>>>> weightedPlaceholderResolvers)
      throws UnscannableMethodException {
    super(proxiedType, classLoader);
    this.placeholderResolverStrategy = placeholderResolverStrategy;
    this.messageSource = messageSource;
    this.messageRenderer = messageRenderer;
    this.messageSender = messageSender;
    this.weightedReceiverLocatorResolvers = Collections.unmodifiableNavigableSet(weightedReceiverLocatorResolvers);
    this.weightedPlaceholderResolvers = Collections.unmodifiableMap(weightedPlaceholderResolvers);
    scanMethods(proxiedTypeKey(), classLoader);
  }

  @Override
  public IPlaceholderResolverStrategy<R, I, F> placeholderResolverStrategy() {
    return this.placeholderResolverStrategy;
  }

  @Override
  public NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>>
      weightedReceiverLocatorResolvers() {
    return this.weightedReceiverLocatorResolvers;
  }

  @Override
  public Map<Type, NavigableSet<Weighted<? extends IPlaceholderResolver<? extends R, ?, ? extends F>>>> weightedPlaceholderResolvers() {
    return this.weightedPlaceholderResolvers;
  }

  @Override
  public IMessageSource<R, I> messageSource() {
    return this.messageSource;
  }

  @Override
  public IMessageRenderer<R, I, O, F> messageRenderer() {
    return this.messageRenderer;
  }

  @Override
  public IMessageSender<R, O> messageSender() {
    return this.messageSender;
  }
}

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
import java.util.Map;
import java.util.NavigableSet;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.message.IMessageRenderer;
import net.kyori.moonshine.message.IMessageSender;
import net.kyori.moonshine.message.IMessageSource;
import net.kyori.moonshine.placeholder.IPlaceholderResolver;
import net.kyori.moonshine.receiver.IReceiverLocatorResolver;
import net.kyori.moonshine.strategy.IPlaceholderResolverStrategy;
import net.kyori.moonshine.util.Weighted;

public final class MoonshineChild<R, I, O, F> extends Moonshine<R, I, O, F> {
  private final Moonshine<R, I, O, F> parent;

  public MoonshineChild(
      final Type proxiedType,
      final ClassLoader proxyClassLoader,
      final Moonshine<R, I, O, F> parent,
      final String inheritedKey)
      throws UnscannableMethodException {
    super(proxiedType, proxyClassLoader);
    this.parent = parent;
    scanMethods(inheritedKey + proxiedTypeKey(), proxyClassLoader);
  }

  @Override
  public Moonshine<R, I, O, F> parent() {
    return this.parent;
  }

  @Override
  public IPlaceholderResolverStrategy<R, I, F> placeholderResolverStrategy() {
    return this.parent.placeholderResolverStrategy();
  }

  @Override
  public NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers() {
    return this.parent.weightedReceiverLocatorResolvers();
  }

  @Override
  public Map<Type, NavigableSet<Weighted<? extends IPlaceholderResolver<? extends R, ?, ? extends F>>>> weightedPlaceholderResolvers() {
    return this.parent.weightedPlaceholderResolvers();
  }

  @Override
  public IMessageSource<R, I> messageSource() {
    return this.parent.messageSource();
  }

  @Override
  public IMessageRenderer<R, I, O, F> messageRenderer() {
    return this.parent.messageRenderer();
  }

  @Override
  public IMessageSender<R, O> messageSender() {
    return this.parent.messageSender();
  }
}

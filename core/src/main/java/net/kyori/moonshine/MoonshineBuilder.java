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

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import net.kyori.moonshine.annotation.meta.NotThreadSafe;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.message.IMessageRenderer;
import net.kyori.moonshine.message.IMessageSender;
import net.kyori.moonshine.message.IMessageSource;
import net.kyori.moonshine.placeholder.IPlaceholderResolver;
import net.kyori.moonshine.receiver.IReceiverLocatorResolver;
import net.kyori.moonshine.strategy.IPlaceholderResolverStrategy;
import net.kyori.moonshine.util.Weighted;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.checkerframework.dataflow.qual.Deterministic;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;

@NotThreadSafe // Technically wrong, but this is a builder.
public final class MoonshineBuilder {
  private MoonshineBuilder() {
  }

  @SideEffectFree
  static <T, R> Receivers<T, R> newBuilder(final TypeToken<T> proxiedType) {
    return new Receivers<>(proxiedType);
  }

  @NotThreadSafe
  public static final class Receivers<T, R> {
    private final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers = new TreeSet<>();

    private final TypeToken<T> proxiedType;

    private Receivers(final TypeToken<T> proxiedType) {
      this.proxiedType = proxiedType;
    }

    @Pure
    public NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers() {
      return this.weightedReceiverLocatorResolvers;
    }

    @Deterministic
    public @This Receivers<T, R> receiverLocatorResolver(
        final Weighted<? extends IReceiverLocatorResolver<? extends R>> weightedReceiverLocatorResolver) {
      this.weightedReceiverLocatorResolvers.add(weightedReceiverLocatorResolver);
      return this;
    }

    @Deterministic
    public @This Receivers<T, R> receiverLocatorResolver(
        final IReceiverLocatorResolver<? extends R> receiverLocatorResolver, final int weight) {
      this.weightedReceiverLocatorResolvers.add(new Weighted<>(receiverLocatorResolver, weight));
      return this;
    }

    @SideEffectFree
    public <I> Sourced<T, R, I> sourced(final IMessageSource<R, I> messageSource) {
      return new Sourced<>(this.proxiedType, this.weightedReceiverLocatorResolvers, messageSource);
    }
  }

  @NotThreadSafe // Technically wrong, but this is a builder.
  public static final class Sourced<T, R, I> {
    private final TypeToken<T> proxiedType;
    private final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers;
    private final IMessageSource<R, I> messageSource;

    private Sourced(final TypeToken<T> proxiedType,
        final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers,
        final IMessageSource<R, I> messageSource) {
      this.proxiedType = proxiedType;
      this.weightedReceiverLocatorResolvers = weightedReceiverLocatorResolvers;
      this.messageSource = messageSource;
    }

    @SideEffectFree
    public <O, F> Rendered<T, R, I, O, F> rendered(final IMessageRenderer<R, I, O, F> messageRenderer) {
      return new Rendered<>(this.proxiedType, this.weightedReceiverLocatorResolvers, this.messageSource,
          messageRenderer);
    }
  }

  @NotThreadSafe // Technically wrong, but this is a builder.
  public static final class Rendered<T, R, I, O, F> {
    private final TypeToken<T> proxiedType;
    private final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers;
    private final IMessageSource<R, I> messageSource;
    private final IMessageRenderer<R, I, O, F> messageRenderer;

    private Rendered(final TypeToken<T> proxiedType,
        final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers,
        final IMessageSource<R, I> messageSource,
        final IMessageRenderer<R, I, O, F> messageRenderer) {
      this.proxiedType = proxiedType;
      this.weightedReceiverLocatorResolvers = weightedReceiverLocatorResolvers;
      this.messageSource = messageSource;
      this.messageRenderer = messageRenderer;
    }

    @SideEffectFree
    public Sent<T, R, I, O, F> sent(final IMessageSender<R, O> messageSender) {
      return new Sent<>(this.proxiedType, this.weightedReceiverLocatorResolvers, this.messageSource,
          this.messageRenderer, messageSender);
    }
  }

  @NotThreadSafe // Technically wrong, but this is a builder.
  public static final class Sent<T, R, I, O, F> {
    private final TypeToken<T> proxiedType;
    private final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers;
    private final IMessageSource<R, I> messageSource;
    private final IMessageRenderer<R, I, O, F> messageRenderer;
    private final IMessageSender<R, O> messageSender;

    private Sent(final TypeToken<T> proxiedType,
        final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers,
        final IMessageSource<R, I> messageSource,
        final IMessageRenderer<R, I, O, F> messageRenderer,
        final IMessageSender<R, O> messageSender) {
      this.proxiedType = proxiedType;
      this.weightedReceiverLocatorResolvers = weightedReceiverLocatorResolvers;
      this.messageSource = messageSource;
      this.messageRenderer = messageRenderer;
      this.messageSender = messageSender;
    }

    @SideEffectFree
    public Resolved<T, R, I, O, F> resolvingWithStrategy(
        final IPlaceholderResolverStrategy<R, I, F> placeholderResolverStrategy) {
      return new Resolved<>(this.proxiedType, this.weightedReceiverLocatorResolvers, this.messageSource,
          this.messageRenderer, this.messageSender, placeholderResolverStrategy);
    }
  }

  @NotThreadSafe
  public static final class Resolved<T, R, I, O, F> {
    private final TypeToken<T> proxiedType;
    private final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers;
    private final IMessageSource<R, I> messageSource;
    private final IMessageRenderer<R, I, O, F> messageRenderer;
    private final IMessageSender<R, O> messageSender;
    private final IPlaceholderResolverStrategy<R, I, F> placeholderResolverStrategy;
    private final Map<Type, NavigableSet<Weighted<? extends IPlaceholderResolver<? extends R, ?, ? extends F>>>>
        weightedPlaceholderResolvers = new HashMap<>();

    private Resolved(final TypeToken<T> proxiedType,
        final NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers,
        final IMessageSource<R, I> messageSource, final IMessageRenderer<R, I, O, F> messageRenderer,
        final IMessageSender<R, O> messageSender,
        final IPlaceholderResolverStrategy<R, I, F> placeholderResolverStrategy) {
      this.proxiedType = proxiedType;
      this.weightedReceiverLocatorResolvers = weightedReceiverLocatorResolvers;
      this.messageSource = messageSource;
      this.messageRenderer = messageRenderer;
      this.messageSender = messageSender;
      this.placeholderResolverStrategy = placeholderResolverStrategy;
    }

    @Deterministic
    public <Z> @This Resolved<T, R, I, O, F> weightedPlaceholderResolver(
        final Class<? extends Z> resolvedType,
        final Weighted<? extends IPlaceholderResolver<? extends R, ? super Z, ? extends F>> weightedPlaceholderResolver) {
      this.weightedPlaceholderResolvers.computeIfAbsent(resolvedType, ignored -> new TreeSet<>())
          .add(weightedPlaceholderResolver);
      return this;
    }

    @Deterministic
    public <Z> @This Resolved<T, R, I, O, F> weightedPlaceholderResolver(
        final TypeToken<? extends Z> resolvedType,
        final Weighted<? extends IPlaceholderResolver<? extends R, ? super Z, ? extends F>> weightedPlaceholderResolver) {
      this.weightedPlaceholderResolvers.computeIfAbsent(resolvedType.getType(), ignored -> new TreeSet<>())
          .add(weightedPlaceholderResolver);
      return this;
    }

    @Deterministic
    public <Z> @This Resolved<T, R, I, O, F> weightedPlaceholderResolver(
        final Class<? extends Z> resolvedType,
        final IPlaceholderResolver<? extends R, ? super Z, ? extends F> placeholderResolver,
        final int weight) {
      this.weightedPlaceholderResolvers.computeIfAbsent(resolvedType, ignored -> new TreeSet<>())
          .add(new Weighted<>(placeholderResolver, weight));
      return this;
    }

    @Deterministic
    public <Z> @This Resolved<T, R, I, O, F> weightedPlaceholderResolver(
        final TypeToken<? extends Z> resolvedType,
        final IPlaceholderResolver<? extends R, ? super Z, ? extends F> placeholderResolver,
        final int weight) {
      this.weightedPlaceholderResolvers.computeIfAbsent(resolvedType.getType(), ignored -> new TreeSet<>())
          .add(new Weighted<>(placeholderResolver, weight));
      return this;
    }

    @SideEffectFree
    public T create() throws UnscannableMethodException {
      return this.create(Thread.currentThread().getContextClassLoader());
    }

    @SuppressWarnings("unchecked") // Proxy returns Object; we expect T which is provided in #proxiedType.
    @SideEffectFree
    public T create(final ClassLoader classLoader) throws UnscannableMethodException {
      final Moonshine<R, I, O, F> moonshine = new Moonshine<>(this.proxiedType, this.placeholderResolverStrategy,
          this.messageSource, this.messageRenderer, this.messageSender, this.weightedReceiverLocatorResolvers,
          this.weightedPlaceholderResolvers);
      return (T) Proxy.newProxyInstance(classLoader,
          new Class[]{GenericTypeReflector.erase(this.proxiedType.getType())},
          moonshine.invocationHandler());
    }
  }
}

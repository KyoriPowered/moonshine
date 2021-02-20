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

package com.proximyst.moonshine;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import com.proximyst.moonshine.component.placeholder.IPlaceholderResolver;
import com.proximyst.moonshine.component.receiver.IReceiverResolver;
import com.proximyst.moonshine.message.IMessageParser;
import com.proximyst.moonshine.message.IMessageSender;
import com.proximyst.moonshine.message.IMessageSource;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.checkerframework.dataflow.qual.SideEffectFree;

public class MoonshineBuilder<B, R> {
  protected final Multimap<Class<?>, IPlaceholderResolver<R, ?>> placeholderResolvers;

  private MoonshineBuilder(final Multimap<Class<?>, IPlaceholderResolver<R, ?>> placeholderResolvers) {
    this.placeholderResolvers = placeholderResolvers;
  }

  static <R> Receivers<R> newBuilder() {
    return new Receivers<>();
  }

  @SuppressWarnings("unchecked")
  public final <T> @This B placeholder(final Class<T> type, final IPlaceholderResolver<R, T> placeholderResolver) {
    this.placeholderResolvers.put(type, placeholderResolver);
    return (B) this;
  }

  public static final class Receivers<R> extends MoonshineBuilder<Receivers<R>, R> {
    private final List<IReceiverResolver<R>> receivers = new ArrayList<>();

    private Receivers() {
      super(ListMultimapBuilder.hashKeys().linkedHashSetValues().build());
    }

    public @This Receivers<R> receiver(final IReceiverResolver<R> resolver) {
      this.receivers.add(resolver);
      return this;
    }

    @SideEffectFree
    public <O> Sourced<R, O> source(final IMessageSource<O, R> messageSource) {
      return new Sourced<>(this.placeholderResolvers, this.receivers, messageSource);
    }
  }

  public static final class Sourced<R, O> extends MoonshineBuilder<Sourced<O, R>, R> {
    private final List<IReceiverResolver<R>> receivers;
    private final IMessageSource<O, R> messageSource;

    private Sourced(final Multimap<Class<?>, IPlaceholderResolver<R, ?>> placeholderResolvers,
        final List<IReceiverResolver<R>> receivers,
        final IMessageSource<O, R> messageSource) {
      super(placeholderResolvers);
      this.receivers = receivers;
      this.messageSource = messageSource;
    }

    public @This Sourced<R, O> receiver(final IReceiverResolver<R> resolver) {
      this.receivers.add(resolver);
      return this;
    }

    @SideEffectFree
    public <M> Parsed<R, O, M> parser(final IMessageParser<O, M, R> messageParser) {
      return new Parsed<>(this.placeholderResolvers, this.receivers, this.messageSource, messageParser);
    }
  }

  public static final class Parsed<R, O, M> extends MoonshineBuilder<Parsed<R, O, M>, R> {
    private final List<IReceiverResolver<R>> receivers;
    private final IMessageSource<O, R> messageSource;
    private final IMessageParser<O, M, R> messageParser;

    private Parsed(final Multimap<Class<?>, IPlaceholderResolver<R, ?>> placeholderResolvers,
        final List<IReceiverResolver<R>> receivers,
        final IMessageSource<O, R> messageSource,
        final IMessageParser<O, M, R> messageParser) {
      super(placeholderResolvers);
      this.receivers = receivers;
      this.messageSource = messageSource;
      this.messageParser = messageParser;
    }

    public @This Parsed<R, O, M> receiver(final IReceiverResolver<R> resolver) {
      this.receivers.add(resolver);
      return this;
    }

    @SideEffectFree
    public Sender<R, O, M> sender(final IMessageSender<R, M> messageSender) {
      return new Sender<>(this.placeholderResolvers,
          this.receivers,
          this.messageSource,
          this.messageParser,
          messageSender);
    }
  }

  public static final class Sender<R, O, M> extends MoonshineBuilder<Sender<R, O, M>, R> {
    private final List<IReceiverResolver<R>> receivers;
    private final IMessageSource<O, R> messageSource;
    private final IMessageParser<O, M, R> messageParser;
    private final IMessageSender<R, M> messageSender;

    private Sender(final Multimap<Class<?>, IPlaceholderResolver<R, ?>> placeholderResolvers,
        final List<IReceiverResolver<R>> receivers,
        final IMessageSource<O, R> messageSource,
        final IMessageParser<O, M, R> messageParser,
        final IMessageSender<R, M> messageSender) {
      super(placeholderResolvers);
      this.receivers = receivers;
      this.messageSource = messageSource;
      this.messageParser = messageParser;
      this.messageSender = messageSender;
    }

    @SideEffectFree
    public <T> T create(final Class<T> type) {
      return this.create(type, Thread.currentThread().getContextClassLoader());
    }

    @SuppressWarnings("unchecked")
    @SideEffectFree
    public <T> T create(final Class<T> type, final ClassLoader classLoader) {
      final Moonshine<R, M, O> moonshine = new Moonshine<>(type,
          this.messageSource,
          this.messageParser,
          this.messageSender,
          this.receivers,
          this.placeholderResolvers);
      return (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{type}, moonshine::proxyInvocation);
    }
  }
}

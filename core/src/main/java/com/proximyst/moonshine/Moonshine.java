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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.proximyst.moonshine.component.placeholder.IPlaceholderResolver;
import com.proximyst.moonshine.component.placeholder.PlaceholderContext;
import com.proximyst.moonshine.component.placeholder.PlaceholderData;
import com.proximyst.moonshine.component.placeholder.ResolveResult;
import com.proximyst.moonshine.component.placeholder.standard.StandardStringPlaceholderResolver;
import com.proximyst.moonshine.component.receiver.IReceiverResolver;
import com.proximyst.moonshine.component.receiver.ReceiverContext;
import com.proximyst.moonshine.component.receiver.StandardReceiverParameterResolver;
import com.proximyst.moonshine.exception.UnresolvablePlaceholderException;
import com.proximyst.moonshine.internal.IFindMethod;
import com.proximyst.moonshine.internal.ThrowableUtils;
import com.proximyst.moonshine.internal.jre8.Java8FindMethod;
import com.proximyst.moonshine.internal.jre9.Java9FindMethod;
import com.proximyst.moonshine.message.IMessageParser;
import com.proximyst.moonshine.message.IMessageSender;
import com.proximyst.moonshine.message.IMessageSource;
import com.proximyst.moonshine.message.ParsingContext;
import com.proximyst.moonshine.proxy.MessageMethod;
import com.proximyst.moonshine.util.CollectionUtils;
import com.proximyst.moonshine.util.ReflectionUtils;
import io.leangen.geantyref.GenericTypeReflector;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

/**
 * This is the entrypoint helper class to moonshine.
 */
public final class Moonshine<R, M, O> {
  private static final Object[] EMPTY_ARRAY = new Object[0];
  private static final boolean IS_JAVA_9 = ReflectionUtils.isJava9();
  private static final IFindMethod FIND_METHOD_UTIL = IS_JAVA_9 ? new Java9FindMethod() : new Java8FindMethod();

  private final Class<?> type;
  private final Map<Method, MessageMethod<R, O>> messageMethods = new HashMap<>();
  private final Multimap<Class<?>, IPlaceholderResolver<R, ?>> placeholderResolvers;
  private final List<IReceiverResolver<R>> receiverResolvers;
  private final IMessageSource<O> messageSource;
  private final IMessageParser<O, M, R> messageParser;
  private final IMessageSender<R, M> messageSender;

  Moonshine(final Class<?> type,
      final IMessageSource<O> messageSource,
      final IMessageParser<O, M, R> messageParser,
      final IMessageSender<R, M> messageSender,
      final List<IReceiverResolver<R>> receiverResolvers,
      final Multimap<Class<?>, IPlaceholderResolver<R, ?>> placeholderResolvers) {
    this.type = type;
    this.messageParser = messageParser;
    this.messageSource = messageSource;
    this.messageSender = messageSender;

    this.receiverResolvers = new ArrayList<>(receiverResolvers);
    this.receiverResolvers.add(0, new StandardReceiverParameterResolver<>());

    final Multimap<Class<?>, IPlaceholderResolver<R, ?>> newPlacholderResolvers = HashMultimap
        .create(placeholderResolvers);
    // TODO(Proximyst): Add default placeholder resolvers
    newPlacholderResolvers.put(String.class, new StandardStringPlaceholderResolver<>());
    newPlacholderResolvers.put(Boolean.class, new StandardStringPlaceholderResolver<>());
    newPlacholderResolvers.put(boolean.class, new StandardStringPlaceholderResolver<>());
    this.placeholderResolvers = newPlacholderResolvers;

    // This has to be done after initialising the placeholders!
    for (final Method method : type.getDeclaredMethods()) {
      if (method.isDefault() || !Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
        continue;
      }

      final MessageMethod<R, O> messageMethod = new MessageMethod<>(method, this);
      this.messageMethods.put(method, messageMethod);
    }
  }

  public static <R> MoonshineBuilder.Receivers<R> builder() {
    return MoonshineBuilder.newBuilder();
  }

  @Pure
  public Collection<IPlaceholderResolver<R, ?>> resolversFor(final AnnotatedType annotatedType) {
    return this.resolversFor(annotatedType.getType());
  }

  @Pure
  public Collection<IPlaceholderResolver<R, ?>> resolversFor(final Type type) {
    return this.placeholderResolvers.get(GenericTypeReflector.erase(type));
  }

  @Pure
  public List<IReceiverResolver<R>> receiverResolvers() {
    return this.receiverResolvers;
  }

  @Nullable Object proxyInvocation(final Object proxy, final Method method, Object @Nullable [] args) {
    if (args == null) {
      args = EMPTY_ARRAY;
    }

    if (ReflectionUtils.isEqualsMethod(method)) {
      return args.length == 1 && proxy == args[0];
    }
    if (ReflectionUtils.isHashCodeMethod(method)) {
      return this.hashCode();
    }
    if (ReflectionUtils.isToStringMethod(method)) {
      return this.type.getName() + "@" + this.hashCode();
    }

    if (method.isDefault()) {
      try {
        final MethodHandle handle = FIND_METHOD_UTIL.findMethod(method, proxy);
        if (args.length == 0) {
          handle.invokeExact(proxy);
        } else {
          handle.invokeWithArguments(args);
        }

        return null;
      } catch (final Throwable throwable) {
        ThrowableUtils.sneakyThrow(throwable);
        throw new RuntimeException(throwable);
      }
    }

    final MessageMethod<R, O> messageMethod = this.messageMethods.get(method);
    if (messageMethod == null) {
      throw new IllegalStateException("unknown message method: " + ReflectionUtils.formatMethod(method));
    }

    final R receiver = messageMethod.receiverLocator().find(new ReceiverContext(method, proxy, args));

    final O rawMessage = this.messageSource.message(messageMethod.messageKey());

    final Table<String, Class<?>, Object> flags = HashBasedTable.create();
    for (final Cell<String, Class<?>, Integer> cell : messageMethod.flags().cellSet()) {
      final Object value = args[cell.getValue()];
      flags.put(Objects.requireNonNull(cell.getRowKey()),
          Objects.requireNonNull(cell.getColumnKey()),
          value == null ? EmptyCell.INSTANCE : value);
    }

    final PlaceholderContext<R> placeholderContext = new PlaceholderContext<>(method, proxy, args, receiver);
    final Map<String, String> placeholders = new HashMap<>();
    final Multimap<String, Object> resolverFlags = HashMultimap.create();
    for (final PlaceholderData placeholderData : messageMethod.placeholders()) {
      Object value = args[placeholderData.index()];
      ResolveResult placeholder;
      do {
        resolverFlags.clear();
        for (final String flag : placeholderData.flags()) {
          Class<?> type = GenericTypeReflector.erase(placeholderData.type().getType());
          while (type != null) {
            final Object flagValue = flags.get(flag, type);
            if (flagValue != null) {
              resolverFlags.put(flag, flagValue == EmptyCell.INSTANCE ? null : flagValue);
            }
            type = type.getSuperclass();
          }
        }

        final Class<?> valueType = value.getClass();
        final List<IPlaceholderResolver<R, ?>> placeholderResolvers = CollectionUtils
            .asList(this.resolversFor(valueType));
        final ListIterator<IPlaceholderResolver<R, ?>> resolverIterator = placeholderResolvers
            .listIterator(placeholderResolvers.size());
        do {
          try {
            placeholder = ((IPlaceholderResolver<R, Object>) resolverIterator.previous())
                .resolve(value, placeholderContext, resolverFlags);
          } catch (final Throwable throwable) {
            if (throwable instanceof Error) {
              throw throwable;
            }

            placeholder = ResolveResult.error(throwable);
          }

          if (placeholder instanceof ResolveResult.Error) {
            throw new UnresolvablePlaceholderException("Placeholder " + placeholderData.name()
                + " of " + ReflectionUtils.formatMethod(method)
                + " cannot be resolved",
                ((ResolveResult.Error) placeholder).throwable());
          }

          if (placeholder instanceof ResolveResult.Ok) {
            value = ((ResolveResult.Ok) placeholder).item();
          }
        } while (resolverIterator.hasPrevious()
            && value.getClass().isAssignableFrom(GenericTypeReflector.erase(placeholderData.type().getType()))
            && !(placeholder instanceof ResolveResult.Finished));

        if (value == null) {
          placeholder = ResolveResult.finished(null);
          break;
        }

        if (placeholder instanceof ResolveResult.Ok && value.getClass().isAssignableFrom(valueType)) {
          placeholder = ResolveResult.finished(String.valueOf(value));
          break;
        }
      } while (!(placeholder instanceof ResolveResult.Finished));

      placeholders.put(placeholderData.name(), String.valueOf(((ResolveResult.Finished) placeholder).item()));
    }

    final ParsingContext<R> parsingContext = new ParsingContext<>(placeholders, receiver);
    final M message = this.messageParser.parse(rawMessage, parsingContext);

    this.messageSender.sendMessage(receiver, message);

    return null;
  }

  private static class EmptyCell {
    private static final EmptyCell INSTANCE = new EmptyCell();

    private EmptyCell() {
    }
  }
}

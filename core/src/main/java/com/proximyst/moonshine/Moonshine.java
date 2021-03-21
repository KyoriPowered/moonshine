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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.proximyst.moonshine.component.placeholder.IPlaceholderResolver;
import com.proximyst.moonshine.component.placeholder.PlaceholderContext;
import com.proximyst.moonshine.component.placeholder.PlaceholderData;
import com.proximyst.moonshine.component.placeholder.ResolveResult;
import com.proximyst.moonshine.component.placeholder.standard.StandardBooleanPlaceholderResolver;
import com.proximyst.moonshine.component.placeholder.standard.StandardCharacterPlaceholderResolver;
import com.proximyst.moonshine.component.placeholder.standard.StandardFlatNumberPlaceholderResolver;
import com.proximyst.moonshine.component.placeholder.standard.StandardFloatingNumberPlaceholderResolver;
import com.proximyst.moonshine.component.placeholder.standard.StandardStringPlaceholderResolver;
import com.proximyst.moonshine.component.receiver.IReceiverResolver;
import com.proximyst.moonshine.component.receiver.ReceiverContext;
import com.proximyst.moonshine.component.receiver.StandardReceiverParameterResolver;
import com.proximyst.moonshine.exception.PlaceholderResolvingErrorResultException;
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;

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
  private final IMessageSource<O, R> messageSource;
  private final IMessageParser<O, M, R> messageParser;
  private final IMessageSender<R, M> messageSender;

  Moonshine(final Class<?> type,
      final IMessageSource<O, R> messageSource,
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

    final Multimap<Class<?>, IPlaceholderResolver<R, ?>> newPlaceholderResolvers = HashMultimap
        .create(placeholderResolvers);
    newPlaceholderResolvers.put(String.class, new StandardStringPlaceholderResolver<>());
    newPlaceholderResolvers.put(Character.class, new StandardCharacterPlaceholderResolver<>());
    newPlaceholderResolvers.put(Boolean.class, new StandardBooleanPlaceholderResolver<>());
    newPlaceholderResolvers.put(Number.class, new StandardFlatNumberPlaceholderResolver<>());
    newPlaceholderResolvers.put(Byte.class, new StandardFlatNumberPlaceholderResolver<>());
    newPlaceholderResolvers.put(Short.class, new StandardFlatNumberPlaceholderResolver<>());
    newPlaceholderResolvers.put(Integer.class, new StandardFlatNumberPlaceholderResolver<>());
    newPlaceholderResolvers.put(Long.class, new StandardFlatNumberPlaceholderResolver<>());
    newPlaceholderResolvers.put(Float.class, new StandardFloatingNumberPlaceholderResolver<>());
    newPlaceholderResolvers.put(Double.class, new StandardFloatingNumberPlaceholderResolver<>());
    this.placeholderResolvers = newPlaceholderResolvers;

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

  @SideEffectFree
  public Collection<IPlaceholderResolver<R, ?>> resolversFor(final AnnotatedType annotatedType) {
    return this.resolversFor(annotatedType.getType());
  }

  @SideEffectFree
  public Collection<IPlaceholderResolver<R, ?>> resolversFor(final Type type) {
    Class<?> erasedType = GenericTypeReflector.erase(GenericTypeReflector.box(type));
    final List<IPlaceholderResolver<R, ?>> resolvers = new ArrayList<>(this.placeholderResolvers.get(erasedType));
    while (erasedType != Object.class && erasedType != null) {
      for (final Class<?> implementedInterface : erasedType.getInterfaces()) {
        resolvers.addAll(0, this.resolversFor(implementedInterface));
      }

      erasedType = erasedType.getSuperclass();
      resolvers.addAll(0, this.placeholderResolvers.get(erasedType));
    }

    return resolvers;
  }

  @Pure
  public List<IReceiverResolver<R>> receiverResolvers() {
    return this.receiverResolvers;
  }

  private Map<String, String> resolvePlaceholder(final PlaceholderContext<R> placeholderContext,
      final Table<String, Class<?>, Object> flags, final Multimap<String, Object> resolverFlags,
      final String placeholderName, final Object value, final PlaceholderData placeholderData) {
    // First we have to collect the applicable flags for the current placeholder value type.
    // We do this by leveraging the provided multi-map, as clearing is faster than allocating.
    resolverFlags.clear();
    for (final String flag : placeholderData.flags()) {
      Class<?> type = GenericTypeReflector.erase(GenericTypeReflector.box(value.getClass()));
      while (type != null) {
        final Object flagValue = flags.get(flag, type);
        if (flagValue != null) {
          resolverFlags.put(flag, flagValue == EmptyCell.INSTANCE ? null : flagValue);
        }
        type = type.getSuperclass();
      }
    }

    // Now we need to actually resolve the placeholder, and re-resolve any new re-resolvable results.
    // To do this, we require the resolvers of this type:
    final Class<?> valueType = value.getClass();
    final List<IPlaceholderResolver<R, ?>> placeholderResolvers = CollectionUtils
        .asList(this.resolversFor(valueType));
    final ListIterator<IPlaceholderResolver<R, ?>> resolverIterator = placeholderResolvers
        .listIterator(placeholderResolvers.size());

    ResolveResult result;
    try {
      do {
        result = ((IPlaceholderResolver<R, Object>) resolverIterator.previous())
            .resolve(placeholderName, value, placeholderContext, resolverFlags);
      } while (result instanceof ResolveResult.Pass && resolverIterator.hasPrevious());
    } catch (final Throwable throwable) {
      // We should never swallow Errors; this is specified in its javadoc.
      if (throwable instanceof Error) {
        // Let's throw up to respect the documented behaviour of Errors.
        throw throwable;
      }

      result = ResolveResult.error(throwable);
    }

    if (result instanceof ResolveResult.Pass) {
      // We've hit the end of resolvers, therefore we have nothing special to set the placeholder as.
      if (value instanceof String) {
        return ImmutableMap.of(placeholderName, String.valueOf(value));
      } else {
        result = ResolveResult.ok(placeholderName, String.valueOf(value));
      }
    }

    if (result instanceof ResolveResult.Finished) {
      return Maps.transformValues(((ResolveResult.Finished) result).items(), String::valueOf);
    }

    if (result instanceof ResolveResult.Error) {
      final Throwable throwable = ((ResolveResult.Error) result).throwable();
      if (throwable instanceof Error // Errors must always be rethrown.
          || throwable instanceof RuntimeException // Unchecked exception, doesn't need to be "able" to throw this.
          || ReflectionUtils.canThrow(placeholderContext.method(), throwable.getClass())) {
        ThrowableUtils.sneakyThrow(throwable);
        throw new RuntimeException(throwable);
      }

      throw new PlaceholderResolvingErrorResultException(throwable);
    }

    // Result must now be Ok.
    // To be 100% sure, let's just ensure this is the case as we don't have sealed classes (yet!).
    if (!(result instanceof ResolveResult.Ok)) {
      throw new IllegalStateException("Result is not Ok; open an issue at https://github.com/Proximyst/moonshine");
    }

    final Map<String, Object> resolvedItems = ((ResolveResult.Ok) result).items();
    if (resolvedItems.isEmpty()) {
      return ImmutableMap.of();
    }

    final Map<String, Object> items = new HashMap<>(resolvedItems);
    final Iterator<Entry<String, Object>> itemIterator = resolvedItems.entrySet().iterator();
    //noinspection WhileLoopReplaceableByForEach - we cannot modify and iterate
    while (itemIterator.hasNext()) {
      final Entry<String, Object> entry = itemIterator.next();
      final Map<String, String> resolved = this.resolvePlaceholder(placeholderContext, flags,
          resolverFlags, entry.getKey(), entry.getValue(), placeholderData);
      items.putAll(resolved);
    }

    return Maps.transformValues(items, String::valueOf);
  }

  @Nullable Object proxyInvocation(final Object proxy, final Method method, Object @Nullable [] args) {
    if (args == null) {
      // We should not allocate an extra array for most usages, and we cannot
      // accept null arguments.
      args = EMPTY_ARRAY;
    }

    // Ensure this is not one of the "default" methods on Object.
    // We will just pretend the proxy is this class; we don't care much about the proxy itself.
    if (ReflectionUtils.isEqualsMethod(method)) {
      return args.length == 1 && proxy == args[0];
    }
    if (ReflectionUtils.isHashCodeMethod(method)) {
      return this.hashCode();
    }
    if (ReflectionUtils.isToStringMethod(method)) {
      return this.type.getName() + "@" + this.hashCode();
    }

    // Is this method a default interface method?
    // We will need to call it like usual in that case, as it has an actual implementation.
    if (method.isDefault()) {
      try {
        final MethodHandle handle = FIND_METHOD_UTIL.findMethod(method, proxy);
        if (args.length == 0) {
          return handle.invokeExact(proxy);
        } else {
          return handle.invokeWithArguments(args);
        }
      } catch (final Throwable throwable) {
        ThrowableUtils.sneakyThrow(throwable);
        throw new RuntimeException(throwable);
      }
    }

    // Get the method data of the called method.
    // This contains information on how to proceed.
    final MessageMethod<R, O> messageMethod = this.messageMethods.get(method);
    if (messageMethod == null) {
      // TODO(Mariell Hoversholm): Is invalid state such as this possible?
      //   Investigate the possibility of such an occurrence.
      throw new IllegalStateException("unknown message method: " + ReflectionUtils.formatMethod(method));
    }

    final R receiver = messageMethod.receiverLocator() != null
        ? messageMethod.receiverLocator().find(new ReceiverContext(method, proxy, args))
        : null;

    // Get the raw message from the message source.
    final O rawMessage = this.messageSource.message(messageMethod.messageKey(), receiver);
    if (rawMessage == null) {
      throw new IllegalStateException("No message for key " + messageMethod.messageKey());
    }

    // We need to find all the available flags on this specific method.
    // We know that the flag indices are going to be within bounds, because they
    //   are directly from the scanned method; it would be invalid state to be
    //   out of bounds here.
    final Table<String, Class<?>, Object> flags = HashBasedTable.create();
    for (final Cell<String, Class<?>, Integer> cell : messageMethod.flags().cellSet()) {
      final Object value = args[Objects.requireNonNull(cell.getValue())];
      flags.put(Objects.requireNonNull(cell.getRowKey()),
          Objects.requireNonNull(cell.getColumnKey()),
          value == null ? EmptyCell.INSTANCE : value);
    }

    // The placeholder context is used to convey information to the resolvers
    //   in an ABI-safe way.
    final PlaceholderContext<R> placeholderContext = new PlaceholderContext<>(method, proxy, args, receiver);

    // Store all final placeholders in here.
    final Map<String, String> placeholders = new HashMap<>();

    final Multimap<String, Object> resolverFlags = HashMultimap.create();
    for (final PlaceholderData placeholderData : messageMethod.placeholders()) {
      final Object value = args[placeholderData.index()];
      placeholders.putAll(this.resolvePlaceholder(placeholderContext, flags, resolverFlags,
          placeholderData.name(), value, placeholderData));
    }

    final ParsingContext<R> parsingContext = new ParsingContext<>(placeholders, receiver);
    final M message = this.messageParser.parse(rawMessage, parsingContext);

    if (Void.TYPE.isAssignableFrom(method.getReturnType())) {
      this.messageSender.sendMessage(receiver, message);
      return null;
    } else {
      return message;
    }
  }

  private static class EmptyCell {
    private static final EmptyCell INSTANCE = new EmptyCell();

    private EmptyCell() {
    }
  }
}

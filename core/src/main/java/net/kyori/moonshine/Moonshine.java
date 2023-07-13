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

import static io.leangen.geantyref.GenericTypeReflector.erase;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import net.kyori.moonshine.annotation.MessageSection;
import net.kyori.moonshine.annotation.meta.ThreadSafe;
import net.kyori.moonshine.exception.MissingMoonshineMethodMappingException;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.message.IMessageRenderer;
import net.kyori.moonshine.message.IMessageSender;
import net.kyori.moonshine.message.IMessageSource;
import net.kyori.moonshine.model.MoonshineMethod;
import net.kyori.moonshine.placeholder.IPlaceholderResolver;
import net.kyori.moonshine.receiver.IReceiverLocatorResolver;
import net.kyori.moonshine.strategy.IPlaceholderResolverStrategy;
import net.kyori.moonshine.util.Weighted;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;

/**
 * The meta class for all of a Moonshine-driven proxy.
 *
 * @param <R> the receiver type
 * @param <I> the intermediate message type
 * @param <O> the output/rendered message type
 * @param <F> the finalised placeholder type, post-resolving
 */
@ThreadSafe
public abstract sealed class Moonshine<R, I, O, F> permits MoonshineRoot, MoonshineChild {
  private final Type proxiedType;
  private final Object proxy;
  private final @Nullable MessageSection sectionAnnotation;

  private @MonotonicNonNull Map<Method, MoonshineMethod<? extends R>> scannedMethods;

  protected Moonshine(final Type proxiedType, final ClassLoader classLoader) {
    this.proxiedType = proxiedType;
    this.sectionAnnotation = erase(proxiedType).getAnnotation(MessageSection.class);

    final var invocationHandler = new MoonshineInvocationHandler<>(this);
    this.proxy = Proxy.newProxyInstance(classLoader,
            new Class[]{GenericTypeReflector.erase(proxiedType)},
            invocationHandler);
  }

  /**
   * Scan the proxied type to see which methods are available and store them in a map
   * @param fullKey the key inherited by parent message sections (if applicable)
   *                plus the key and delimiter present on this message section.
   */
  @EnsuresNonNull("scannedMethods")
  protected void scanMethods(final String fullKey, final ClassLoader proxyClassLoader) throws UnscannableMethodException {
    final Method[] methods = erase(this.proxiedType).getMethods();
    final Map<Method, MoonshineMethod<? extends R>> scannedMethods = new HashMap<>(methods.length);
    for (final Method method : methods) {
      if (method.isDefault() || method.getReturnType() == Moonshine.class) {
        continue;
      }

      final MoonshineMethod<? extends R> moonshineMethod =
              new MoonshineMethod<>(this, this.proxiedType, proxyClassLoader, method, fullKey, this.proxiedTypeKeyDelimiter());
      scannedMethods.put(method, moonshineMethod);
    }
    this.scannedMethods = Collections.unmodifiableMap(scannedMethods);
  }

  @SideEffectFree
  public static <T, R> MoonshineBuilder.Receivers<T, R> builder(final TypeToken<T> proxiedType) {
    return MoonshineBuilder.newBuilder(proxiedType);
  }

  /**
   * @return the type which is being proxied with this instance
   */
  @Pure
  public Type proxiedType() {
    return this.proxiedType;
  }

  /**
   * Returns the Moonshine instance that represents the parent of this message section (if it has any).
   * @return the Moonshine instance if any, otherwise null
   */
  @Pure
  public @Nullable Moonshine<R, I, O, F> parent() {
    return null;
  }

  protected String proxiedTypeKey() {
    if (this.sectionAnnotation == null || this.sectionAnnotation.value().isEmpty()) {
      return "";
    }
    return this.sectionAnnotation.value() + this.sectionAnnotation.delimiter();
  }

  protected char proxiedTypeKeyDelimiter() {
    return this.sectionAnnotation != null ? this.sectionAnnotation.delimiter() : MessageSection.DEFAULT_DELIMITER;
  }

  @Pure
  public Object proxy() {
    return this.proxy;
  }

  /**
   * @return the current placeholder resolving strategy
   */
  @Pure
  public abstract IPlaceholderResolverStrategy<R, I, F> placeholderResolverStrategy();

  /**
   * @return an unmodifiable view of a navigable set for iterating through the available {@link
   * IReceiverLocatorResolver}s with weight-based ordering
   */
  @Pure
  public abstract NavigableSet<Weighted<? extends IReceiverLocatorResolver<? extends R>>> weightedReceiverLocatorResolvers();

  /**
   * @return an unmodifiable view of a map of types to navigable sets for iterating through the available {@link
   * IPlaceholderResolver}s with weight-based ordering
   */
  @Pure
  public abstract Map<Type, NavigableSet<Weighted<? extends IPlaceholderResolver<? extends R, ?, ? extends F>>>> weightedPlaceholderResolvers();

  /**
   * Find a scanned method by the given method mapping.
   *
   * @param method the method to find a scanned method for
   * @return the scanned method
   * @throws MissingMoonshineMethodMappingException if a method mapping is missing somehow; this shouldn't happen, but
   * is here just in case
   */
  public MoonshineMethod<? extends R> scannedMethod(final Method method) throws MissingMoonshineMethodMappingException {
    final var scanned = this.scannedMethods.get(method);
    if (scanned == null) {
      throw new MissingMoonshineMethodMappingException(this.proxiedType(), method);
    }

    return scanned;
  }

  /**
   * @return the source of intermediate messages, per receiver
   */
  public abstract IMessageSource<R, I> messageSource();

  /**
   * @return the renderer of messages, used before sending via {@link #messageSender()}
   */
  public abstract IMessageRenderer<R, I, O, F> messageRenderer();

  /**
   * @return the message sender of intermediate messages to a given receiver with resolved placeholders
   */
  public abstract IMessageSender<R, O> messageSender();
}

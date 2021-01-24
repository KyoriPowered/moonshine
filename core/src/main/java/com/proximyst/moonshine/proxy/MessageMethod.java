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

package com.proximyst.moonshine.proxy;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.proximyst.moonshine.Moonshine;
import com.proximyst.moonshine.annotation.Flag;
import com.proximyst.moonshine.annotation.Message;
import com.proximyst.moonshine.annotation.Placeholder;
import com.proximyst.moonshine.component.placeholder.PlaceholderData;
import com.proximyst.moonshine.component.receiver.IReceiver;
import com.proximyst.moonshine.component.receiver.IReceiverResolver;
import com.proximyst.moonshine.exception.UnscannableMethodException;
import com.proximyst.moonshine.util.ReflectionUtils;
import io.leangen.geantyref.GenericTypeReflector;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MessageMethod<R, M> {
  private final String messageKey;
  private final List<PlaceholderData> placeholders;
  private final IReceiver<R> receiverLocator;
  private final Table<String, Class<?>, Integer> flags;

  public MessageMethod(final Method method, final Moonshine<R, ?, M> moonshine) {
    // First we need to scan the method to ensure it's valid at all.

    if (!Void.TYPE.isAssignableFrom(method.getReturnType())) {
      throw new UnscannableMethodException("Return type is not void or Void on "
          + ReflectionUtils.formatMethod(method));
    }

    // We need to scan the method for its metadata.

    final Message messageAnnotation = method.getAnnotation(Message.class);
    if (messageAnnotation == null) {
      throw new UnscannableMethodException("Missing @Message annotation on " + ReflectionUtils.formatMethod(method));
    }

    final List<PlaceholderData> placeholders = new ArrayList<>();
    final Parameter[] parameters = method.getParameters();
    for (int i = 0; i < parameters.length; i++) {
      final Parameter parameter = parameters[i];
      final PlaceholderData data = this.findPlaceholder(parameter, i);
      if (data != null) {
        if (moonshine.resolversFor(data.type()).isEmpty()) {
          throw new UnscannableMethodException("No placeholder resolver for "
              + ReflectionUtils.formatMethod(method)
              + " placeholder "
              + data.name()
              + ", type "
              + GenericTypeReflector.erase(data.type().getType()));
        }
        placeholders.add(data);
      }
    }

    IReceiver<R> receiverLocator = null;
    final ListIterator<IReceiverResolver<R>> resolvers = moonshine.receiverResolvers()
        .listIterator(moonshine.receiverResolvers().size());
    while (resolvers.hasPrevious()) {
      receiverLocator = resolvers.previous().resolve(method).orElse(null);
      if (receiverLocator != null) {
        break;
      }
    }
    if (receiverLocator == null) {
      throw new UnscannableMethodException("Missing viable IReceiver on " + ReflectionUtils.formatMethod(method));
    }

    final Table<String, Class<?>, Integer> flags = HashBasedTable.create();
    for (int i = 0; i < parameters.length; i++) {
      final Parameter parameter = parameters[i];
      final Flag flag = parameter.getAnnotation(Flag.class);
      if (flag != null) {
        flags.put(flag.name(), flag.type(), i);
      }
    }

    // ... and now to assign the metadata.

    this.messageKey = messageAnnotation.value();
    this.placeholders = Collections.unmodifiableList(placeholders);
    this.receiverLocator = receiverLocator;
    this.flags = flags;
  }

  public String messageKey() {
    return this.messageKey;
  }

  public List<PlaceholderData> placeholders() {
    return this.placeholders;
  }

  public IReceiver<R> receiverLocator() {
    return this.receiverLocator;
  }

  public Table<String, Class<?>, Integer> flags() {
    return this.flags;
  }

  private @Nullable PlaceholderData findPlaceholder(final Parameter parameter, final int index) {
    final Placeholder placeholderAnnotation = parameter.getAnnotation(Placeholder.class);
    if (placeholderAnnotation == null) {
      return null;
    }

    String name = placeholderAnnotation.value();
    if (name.trim().isEmpty()) {
      name = parameter.getName();
    }

    final String[] flags = placeholderAnnotation.flags();
    final AnnotatedType type = parameter.getAnnotatedType();

    return new PlaceholderData(name, type, flags, index);
  }
}

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
package net.kyori.moonshine.message;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * A standard formatter for strings using {@link String#replace(CharSequence, CharSequence)} with {@code
 * "${prefix}${value}${suffix}"} where {@code value} is converted using {@link #placeholderValueToStringConverter}.
 * <p>
 * Most people will want a more advanced formatter for Discord messages/embeds, Minecraft component messages, etc.
 */
public record StringMessageRenderer<R, I, O, F>(
    String prefix,
    String suffix,
    Function<I, String> intermediateToStringConverter,
    Function<String, O> stringToOutputConverter,
    Function<F, String> placeholderValueToStringConverter
) implements IMessageRenderer<R, I, O, F> {
  @Override
  public O render(
      final R receiver,
      final I intermediateMessage,
      final Map<String, ? extends F> resolvedPlaceholders,
      final Method method,
      final Type owner
  ) {
    var intermediate = this.intermediateToStringConverter.apply(intermediateMessage);
    for (final Entry<String, ? extends F> entry : resolvedPlaceholders.entrySet()) {
      intermediate = intermediate.replace(this.prefix + entry.getKey() + this.suffix,
          this.placeholderValueToStringConverter.apply(entry.getValue()));
    }
    return this.stringToOutputConverter.apply(intermediate);
  }
}

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
package net.kyori.moonshine.exception;

import net.kyori.moonshine.internal.ReflectiveUtils;
import net.kyori.moonshine.model.MoonshineMethod;

public final class UnfinishedPlaceholderException extends PlaceholderResolvingException {
  private final MoonshineMethod<?> moonshineMethod;
  private final String placeholderName;
  private final Object placeholderValue;

  public UnfinishedPlaceholderException(final MoonshineMethod<?> moonshineMethod, final String placeholderName,
      final Object placeholderValue) {
    super("The placeholder "
        + placeholderName
        + " was unfinished in method: "
        + ReflectiveUtils.formatMethodName(moonshineMethod.owner(), moonshineMethod.reflectMethod()));
    this.moonshineMethod = moonshineMethod;
    this.placeholderName = placeholderName;
    this.placeholderValue = placeholderValue;
  }

  public MoonshineMethod<?> moonshineMethod() {
    return this.moonshineMethod;
  }

  public String placeholderName() {
    return this.placeholderName;
  }

  public Object placeholderValue() {
    return this.placeholderValue;
  }
}

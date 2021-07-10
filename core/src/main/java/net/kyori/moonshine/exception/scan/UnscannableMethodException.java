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

package net.kyori.moonshine.exception.scan;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import net.kyori.moonshine.exception.MoonshineException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

public abstract class UnscannableMethodException extends MoonshineException {
  private final Type owner;
  private final Method method;

  protected UnscannableMethodException(final Type owner, final Method method) {
    this.owner = owner;
    this.method = method;
  }

  protected UnscannableMethodException(final Type owner, final Method method,
      final @Nullable String message) {
    super(message);
    this.owner = owner;
    this.method = method;
  }

  protected UnscannableMethodException(final Type owner, final Method method,
      final @Nullable String message,
      final @Nullable Throwable cause) {
    super(message, cause);
    this.owner = owner;
    this.method = method;
  }

  protected UnscannableMethodException(final Type owner, final Method method,
      final @Nullable Throwable cause) {
    super(cause);
    this.owner = owner;
    this.method = method;
  }

  protected UnscannableMethodException(final Type owner, final Method method,
      final @Nullable String message,
      final @Nullable Throwable cause, final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.owner = owner;
    this.method = method;
  }

  @Pure
  public Type owner() {
    return this.owner;
  }

  @Pure
  public Method method() {
    return this.method;
  }
}

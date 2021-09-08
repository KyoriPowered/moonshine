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
package net.kyori.moonshine.placeholder;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import net.kyori.moonshine.annotation.meta.ThreadSafe;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;

/**
 * A continuing, yet-to-be-fully-resolved value with a known type.
 *
 * @param <F> the type of the value
 */
@ThreadSafe
public final class ContinuanceValue<F> extends ResolvingValue<F> {
  private final Type type;

  private ContinuanceValue(final F value, final Type type) {
    super(value);
    this.type = type;

    if (!GenericTypeReflector.erase(type).isAssignableFrom(value.getClass())) {
      throw new IllegalArgumentException("value must be assignable from "
          + type.getTypeName() + "; found " + value.getClass().getName());
    }
  }

  @SideEffectFree
  public static <F> ContinuanceValue<F> continuanceValue(final F value, final Type type) {
    return new ContinuanceValue<>(value, type);
  }

  @SideEffectFree
  public static <F> ContinuanceValue<F> continuanceValue(final F value, final AnnotatedType type) {
    return new ContinuanceValue<>(value, type.getType());
  }

  @SideEffectFree
  public static <F> ContinuanceValue<F> continuanceValue(final F value, final TypeToken<? extends F> type) {
    return new ContinuanceValue<>(value, type.getType());
  }

  /**
   * @return the type of the continuing value
   */
  @Pure
  public Type type() {
    return this.type;
  }
}

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

package com.proximyst.moonshine.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.TargetLocations;
import org.checkerframework.framework.qual.TypeUseLocation;

/**
 * This defines a placeholder on a message.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
@TargetLocations(TypeUseLocation.PARAMETER)
public @interface Placeholder {
  /**
   * The name of the placeholder.
   * <p>
   * If the code was compiled with {@code -parameters}, moonshine can infer this.
   * </p>
   *
   * @return the name of the placeholder.
   */
  String value() default "";

  /**
   * Flag names to assign the placeholder.
   * <p>
   * This requires {@link Flag} annotations on other parameters.
   * </p>
   * <p>
   * <b>Note:</b> If the given flag name does not support the type of this placeholder, it will not apply to its type.
   * It may still apply to any subsequent types returned by the resolver of this type.
   * </p>
   *
   * @return the names of the flags to assign to this placeholder.
   */
  String[] flags() default {};
}

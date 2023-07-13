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
package net.kyori.moonshine.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This allows developers to have nested messages and configure given properties of a message
 * section.<br>
 * For the root section adding this annotation is optional, but for nested sections it's not.<br>
 * <br>
 * Examples:<br>
 * A message with key 'hello.world-moonshine' could be added as follows:
 *
 * <pre>{@code
 * @MessageSection("hello")
 * public interface HelloSection {
 *     WorldSection world();
 *
 *     @MessageSection("world", delimiter='-')
 *     interface WorldSection {
 *         @Message("moonshine")
 *         String moonshine(@Receiver User user);
 *     }
 * }
 * }</pre>
 *
 * An example where there is no message section annotation on the root section. The following
 * example results in the key 'hello.world':
 *
 * <pre>{@code
 * public interface RootSection {
 *     HelloSection hello();
 *
 *     @MessageSection("hello")
 *     interface HelloSection {
 *         @Message("world")
 *         String world(@Receiver User user);
 *     }
 * }
 * }</pre>
 *
 * Adding the {@link Message @Message} annotation to the method referencing a message section is
 * supported. As well as having an empty key to prepend. The following example results in the key
 * 'hello.world-moonshine':
 *
 * <pre>{@code
 * @MessageSection("hello")
 * public interface HelloSection {
 *     @Message("world")
 *     WorldSection world();
 *
 *     @MessageSection(delimiter='-')
 *     interface WorldSection {
 *         @Message("moonshine")
 *         String moonshine(@Receiver User user);
 *     }
 * }
 * }</pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageSection {
  /** The default delimiter */
  char DEFAULT_DELIMITER = '.';

  /** Returns the message key to prepend to all messages in this section. */
  String value() default "";

  /** Returns the delimiter. */
  char delimiter() default DEFAULT_DELIMITER;
}

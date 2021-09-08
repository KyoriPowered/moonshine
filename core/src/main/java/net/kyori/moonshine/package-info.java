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
@DefaultQualifier(
    value = NonNull.class,
    locations = {
        TypeUseLocation.CONSTRUCTOR_RESULT,
        TypeUseLocation.EXCEPTION_PARAMETER,
        TypeUseLocation.EXPLICIT_LOWER_BOUND,
        TypeUseLocation.EXPLICIT_UPPER_BOUND,
        TypeUseLocation.FIELD,
        TypeUseLocation.IMPLICIT_LOWER_BOUND,
        TypeUseLocation.IMPLICIT_UPPER_BOUND,
        TypeUseLocation.LOWER_BOUND,
        TypeUseLocation.PARAMETER,
        TypeUseLocation.RECEIVER,
        TypeUseLocation.RESOURCE_VARIABLE,
        TypeUseLocation.RETURN,
        TypeUseLocation.UPPER_BOUND,
        TypeUseLocation.OTHERWISE,
    }
)
package net.kyori.moonshine;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;

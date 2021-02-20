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

package com.proximyst.moonshine.component.placeholder.standard;

import com.google.common.collect.Multimap;
import com.proximyst.moonshine.component.placeholder.IPlaceholderResolver;
import com.proximyst.moonshine.component.placeholder.PlaceholderContext;
import com.proximyst.moonshine.component.placeholder.ResolveResult;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class StandardCharacterPlaceholderResolver<R> implements IPlaceholderResolver<R, Character> {
  @Override
  public ResolveResult resolve(final String placeholderName, Character value,
      final PlaceholderContext<R> ctx, final Multimap<String, @Nullable Object> flags) {
    if (flags.isEmpty()) {
      return ResolveResult.pass();
    }

    if (flags.containsEntry("uppercase", true)) {
      value = Character.toUpperCase(value);
    }

    if (flags.containsEntry("lowercase", true)) {
      value = Character.toLowerCase(value);
    }

    return ResolveResult.ok(placeholderName, String.valueOf(value));
  }
}

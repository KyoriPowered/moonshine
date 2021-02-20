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

public final class StandardFloatingNumberPlaceholderResolver<N extends Number, R> implements
    IPlaceholderResolver<R, N> {
  @Override
  public ResolveResult resolve(final String placeholderName, final N value,
      final PlaceholderContext<R> ctx, final Multimap<String, @Nullable Object> flags) {
    double val = value.doubleValue();
    if (flags.isEmpty()) {
      return ResolveResult.pass();
    }

    if (flags.containsEntry("positive", true)) {
      val = Math.abs(val);
    }

    if (flags.containsEntry("negative", true)) {
      val = -Math.abs(val);
    }

    if (flags.containsEntry("flip", true)) {
      val = -val;
    }

    if (flags.containsEntry("binary", true)) {
      return ResolveResult.ok(placeholderName, Long.toBinaryString(Double.doubleToLongBits(val)));
    }

    if (flags.containsEntry("hexadecimal", true) || flags.containsEntry("hex", true)) {
      return ResolveResult.ok(placeholderName, Double.toHexString(val));
    }

    return ResolveResult.ok(placeholderName, Double.toString(val));
  }
}

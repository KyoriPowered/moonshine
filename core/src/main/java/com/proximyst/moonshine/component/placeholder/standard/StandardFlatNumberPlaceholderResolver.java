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
import com.proximyst.moonshine.util.CollectionUtils;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.Nullable;

public class StandardFlatNumberPlaceholderResolver<N extends Number, R> implements IPlaceholderResolver<R, N> {
  @Override
  public ResolveResult resolve(final N value, final PlaceholderContext<R> ctx,
      final Multimap<String, @Nullable Object> flags) {
    long val = value.longValue();
    if (flags.isEmpty()) {
      return ResolveResult.ok(Long.toString(val));
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

    if (flags.containsEntry("hexadecimal", true) || flags.containsEntry("hex", true)) {
      return ResolveResult.ok(Long.toHexString(val));
    }

    if (flags.containsEntry("octal", true)) {
      return ResolveResult.ok(Long.toOctalString(val));
    }

    final Collection<Object> radii = flags.get("radix");
    final Object radix = CollectionUtils.last(radii);
    if (radix != null) {
      if (radix instanceof Number) {
        return ResolveResult.ok(Long.toString(val, ((Number) radix).intValue()));
      } else if (radix instanceof String) {
        // The NumberFormatException is propagated.
        return ResolveResult.ok(Long.toString(val, Integer.parseInt((String) radix)));
      }
    }

    return ResolveResult.ok(Long.toString(val));
  }
}

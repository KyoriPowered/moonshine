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

package com.proximyst.moonshine.util;

import static org.assertj.core.api.Assertions.fail;

import com.google.common.collect.Multimap;
import com.proximyst.moonshine.component.placeholder.IPlaceholderResolver;
import com.proximyst.moonshine.component.placeholder.PlaceholderContext;
import com.proximyst.moonshine.component.placeholder.ResolveResult;
import com.proximyst.moonshine.internal.ThrowableUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RethrowingPlaceholderResolver<R, T extends Throwable> implements IPlaceholderResolver<R, T> {
  @Override
  public ResolveResult resolve(final String placeholderName, final T value, final PlaceholderContext<R> ctx,
      final Multimap<String, @Nullable Object> flags) {
    ThrowableUtils.sneakyThrow(value);
    fail("value not rethrown?");
    throw new AssertionError();
  }
}

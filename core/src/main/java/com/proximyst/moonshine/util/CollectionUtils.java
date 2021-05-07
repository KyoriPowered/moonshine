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

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class CollectionUtils {
  private CollectionUtils() {
  }

  public static <T> @Nullable T last(final @Nullable Collection<T> collection) {
    if (collection == null) {
      return null;
    }

    if (!collection.isEmpty()
        && collection instanceof List
        && collection instanceof RandomAccess) {
      return ((List<T>) collection).get(collection.size() - 1);
    }

    @Nullable T value = null;
    final Iterator<T> iterator = collection.iterator();
    //noinspection WhileLoopReplaceableByForEach
    while (iterator.hasNext()) {
      value = iterator.next();
    }
    return value;
  }

  @SuppressWarnings("unchecked")
  public static <T> List<T> asList(final Iterable<? extends T> iterable) {
    if (iterable instanceof List) {
      return (List<T>) iterable;
    }

    return Lists.newArrayList(iterable);
  }
}

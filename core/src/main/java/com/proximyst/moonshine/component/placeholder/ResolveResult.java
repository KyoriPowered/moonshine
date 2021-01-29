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

package com.proximyst.moonshine.component.placeholder;

import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A result of a resolving.
 * <p>
 * To pass on the resolving to the next resolver for the given type, return {@link #pass()}.
 * <br> To return an error of some kind to the call, return {@link #error(Throwable)}.
 * <br> To return a resolved value that can be passed on to the next resolver for the given type, return {@link
 * #ok(Object)}.
 * <br> To return a fully resolved value that may no longer be passed on any further whatsoever, return {@link
 * #finished(String)}. This is a discouraged result, as {@link #ok(Object)} should rather be preferred.
 */
public abstract class ResolveResult {
  private ResolveResult() {
  }

  public static Pass pass() {
    return Pass.INSTANCE;
  }

  public static Error error(final Throwable throwable) {
    return new Error(throwable);
  }

  public static Ok ok(final Object item) {
    return new Ok(item);
  }

  public static Finished finished(final @Nullable String item) {
    return new Finished(item);
  }

  public static final class Pass extends ResolveResult {
    private static final Pass INSTANCE = new Pass();

    private Pass() {
    }

    @Override
    public boolean equals(final Object obj) {
      return obj instanceof Pass;
    }

    @Override
    public String toString() {
      return "ResolveResult.Pass";
    }
  }

  public static final class Error extends ResolveResult {
    private final Throwable throwable;

    private Error(final Throwable throwable) {
      this.throwable = throwable;
    }

    public Throwable throwable() {
      return this.throwable;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || this.getClass() != o.getClass()) {
        return false;
      }
      final Error error = (Error) o;
      return Objects.equals(this.throwable(), error.throwable());
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.throwable());
    }

    @Override
    public String toString() {
      return "ResolveResult.Error{throwable=" + this.throwable() + '}';
    }
  }

  public static final class Ok extends ResolveResult {
    private final Object item;

    private Ok(final Object item) {
      this.item = item;
    }

    public Object item() {
      return this.item;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || this.getClass() != o.getClass()) {
        return false;
      }
      final Ok ok = (Ok) o;
      return this.item().equals(ok.item());
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.item());
    }

    @Override
    public String toString() {
      return "ResolveResult.Ok{item=" + this.item() + '}';
    }
  }

  public static final class Finished extends ResolveResult {
    private final @Nullable String item;

    private Finished(final @Nullable String item) {
      this.item = item;
    }

    public @Nullable String item() {
      return this.item;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || this.getClass() != o.getClass()) {
        return false;
      }
      final Finished ok = (Finished) o;
      return Objects.equals(this.item(), ok.item());
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.item());
    }

    @Override
    public String toString() {
      return "ResolveResult.Finished{item=" + this.item() + '}';
    }
  }
}

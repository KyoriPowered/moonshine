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

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;

/**
 * A result of a resolving.
 * <p>
 * To pass on the resolving to the next resolver for the given type, return {@link #pass()}.
 * <br> To return an error of some kind to the call, return {@link #error(Throwable)}.
 * <br> To return resolved values that can be passed on to the next resolver for the given types, return {@link
 * #ok(Map)}.
 * <br> To return fully resolved values that may no longer be passed on any further whatsoever, return {@link
 * #finished(Map)}. This is a discouraged result, as {@link #ok(Map)} should rather be preferred.
 */
public abstract class ResolveResult {
  private ResolveResult() {
  }

  /**
   * Pass to the next placeholder resolver in this chain.
   * <p>
   * This does not permanently disregard the current resolver: it only moves on until it moves to another type, then
   * back.
   *
   * @return The {@link Pass} instance, indicating that the next resolver should handle the current value.
   */
  public static Pass pass() {
    return Pass.INSTANCE;
  }

  /**
   * Creates a new {@link Error}, terminating the resolving and execution with an error.
   * <p>
   * This is thrown from the interface method to the caller.
   *
   * @param throwable The error to throw to the caller.
   * @return A new {@link Error} instance with the given {@link Throwable} as the thrown exception.
   */
  public static Error error(final Throwable throwable) {
    return new Error(throwable);
  }

  /**
   * Passes the named {@link Object}s on for another iteration of placeholder resolvers.
   *
   * @param items The items to pass on to the next iteration. This and its values cannot be {@code null}.
   * @return The {@link Ok} instance, indicating that it should not terminate.
   */
  public static Ok ok(final Map<String, Object> items) {
    return new Ok(items);
  }

  /**
   * Passes the named {@link Object} on for another iteration of placeholder resolvers.
   *
   * @param name  The name of the object. This cannot be {@code null}.
   * @param value The value of the object. This cannot be {@code null}.
   * @return The {@link Ok} instance, indicating that it should not terminate.
   */
  public static Ok ok(final String name, final Object value) {
    return ok(ImmutableMap.of(name, value));
  }

  /**
   * Accepts the {@link Object}s as the final placeholders for this resolver, terminating the resolving.
   *
   * @param items The items to accept as the final placeholders. This and its values cannot be {@code null}.
   * @return The {@link Finished} instance, indicating that it should terminate.
   */
  public static Finished finished(final Map<String, Object> items) {
    return new Finished(items);
  }

  /**
   * Accepts the {@link Object} as the final placeholder for this resolver, terminating the resolving.
   *
   * @param name  The name of the object. This cannot be {@code null}.
   * @param value The value of the object. This cannot be {@code null}.
   * @return The {@link Finished} instance, indicating that it should terminate.
   */
  public static Finished finished(final String name, final Object value) {
    return finished(ImmutableMap.of(name, value));
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
    private final Map<String, Object> items;

    private Ok(final Map<String, Object> items) {
      this.items = items;
    }

    public Map<String, Object> items() {
      return this.items;
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
      return this.items().equals(ok.items());
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.items());
    }

    @Override
    public String toString() {
      return "ResolveResult.Ok{items=" + this.items() + '}';
    }
  }

  public static final class Finished extends ResolveResult {
    private final Map<String, Object> items;

    private Finished(final Map<String, Object> items) {
      this.items = items;
    }

    public Map<String, Object> items() {
      return this.items;
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
      return Objects.equals(this.items(), ok.items());
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.items());
    }

    @Override
    public String toString() {
      return "ResolveResult.Finished{items=" + this.items() + '}';
    }
  }
}

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

package net.kyori.moonshine.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.concurrent.ThreadSafe;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;

/**
 * Either the left or the right value.
 *
 * @param <L> the left type
 * @param <R> the right type
 */
@ThreadSafe
public final class Either<L, R> {
  private final @Nullable L left;
  private final @Nullable R right;

  private Either(final @Nullable L left, final @Nullable R right) {
    if ((left == null) == (right == null)) {
      throw new IllegalArgumentException("must be either left or right");
    }

    this.left = left;
    this.right = right;
  }

  @SideEffectFree
  public static <L, R> Either<L, R> left(final L left) {
    return new Either<>(left, null);
  }

  @SideEffectFree
  public Optional<L> left() {
    return Optional.ofNullable(this.leftRaw());
  }

  @SideEffectFree
  public static <L, R> Either<L, R> right(final R right) {
    return new Either<>(null, right);
  }

  @SideEffectFree
  public Optional<R> right() {
    return Optional.ofNullable(this.rightRaw());
  }

  @Pure
  public @Nullable L leftRaw() {
    return this.left;
  }

  @Pure
  public @Nullable R rightRaw() {
    return this.right;
  }

  @EnsuresNonNullIf(expression = "this.left", result = true)
  @Pure
  public boolean isLeft() {
    return this.left != null;
  }

  @EnsuresNonNullIf(expression = "this.right", result = true)
  @Pure
  public boolean isRight() {
    return this.right != null;
  }

  @Pure
  public void ifLeft(final Consumer<L> leftConsumer) {
    if (this.leftRaw() != null) {
      leftConsumer.accept(this.leftRaw());
    }
  }

  @Pure
  public void ifRight(final Consumer<R> rightConsumer) {
    if (this.rightRaw() != null) {
      rightConsumer.accept(this.rightRaw());
    }
  }

  @Pure
  public void map(final Consumer<L> leftConsumer, final Consumer<R> rightConsumer) {
    if (this.leftRaw() != null) {
      leftConsumer.accept(this.leftRaw());
    } else if (this.rightRaw() != null) {
      rightConsumer.accept(this.rightRaw());
    } else {
      throw new IllegalStateException("either left or right must be non-null");
    }
  }

  @Pure
  @Override
  public boolean equals(final @Nullable Object other) {
    if (!(other instanceof Either)) {
      return false;
    }

    final Either<?, ?> otherEither = (Either<?, ?>) other;
    return Objects.equals(otherEither.leftRaw(), this.leftRaw())
        && Objects.equals(otherEither.rightRaw(), this.rightRaw());
  }

  @Pure
  @Override
  public int hashCode() {
    if (this.isLeft()) {
      return this.leftRaw().hashCode();
    } else if (this.isRight()) {
      return this.rightRaw().hashCode();
    } else {
      throw new IllegalStateException("either left or right must be non-null");
    }
  }

  @Override
  public String toString() {
    return "Either{" +
        "left=" + this.leftRaw() +
        ", right=" + this.rightRaw() +
        '}';
  }
}

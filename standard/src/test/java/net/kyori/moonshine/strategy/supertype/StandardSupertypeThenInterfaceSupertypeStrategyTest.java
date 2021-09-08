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
package net.kyori.moonshine.strategy.supertype;

import static org.assertj.core.api.Assertions.assertThat;

import io.leangen.geantyref.TypeToken;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/* package-private */ class StandardSupertypeThenInterfaceSupertypeStrategyTest {
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void simpleSupertypes(final boolean returnObject) {
    final ISupertypeStrategy strategy = new StandardSupertypeThenInterfaceSupertypeStrategy(
        returnObject);
    final Iterator<Type> supertypeIterator = strategy.hierarchyIterator(SimpleSubtypeB.class);

    assertThat(supertypeIterator.next()).isEqualTo(SimpleSubtypeA.class);
    assertThat(supertypeIterator.next()).isEqualTo(SimpleSupertype.class);
    if (returnObject) {
      assertThat(supertypeIterator.next()).isEqualTo(Object.class);
    }
    assertThat(supertypeIterator.hasNext()).isFalse();
  }

  /* package-private */ class SimpleSupertype {
  }

  /* package-private */ class SimpleSubtypeA extends SimpleSupertype {
  }

  /* package-private */ class SimpleSubtypeB extends SimpleSubtypeA {
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void simpleIdealCase(final boolean returnObject) {
    final ISupertypeStrategy strategy = new StandardSupertypeThenInterfaceSupertypeStrategy(
        returnObject);
    final Iterator<Type> supertypeIterator = strategy
        .hierarchyIterator(new TypeToken<SimpleIdealCaseSubSubtype<List<Integer>>>() {
        }.getType());

    assertThat(supertypeIterator.next())
        .isEqualTo(new TypeToken<SimpleIdealCaseSubtype<List<Integer>>>() {
        }.getType());
    assertThat(supertypeIterator.next()).isEqualTo(SimpleIdealCaseSuperType.class);
    if (returnObject) {
      assertThat(supertypeIterator.next()).isEqualTo(Object.class);
    }
    assertThat(supertypeIterator.next())
        .isEqualTo(new TypeToken<SimpleIdealCaseInterfaceShared<List<Integer>>>() {
        }.getType());
    assertThat(supertypeIterator.next()).isEqualTo(SimpleIdealCaseInterfaceSharedSuper.class);
    assertThat(supertypeIterator.next()).isEqualTo(SimpleIdealCaseSuperTypeInterfaceA.class);
    assertThat(supertypeIterator.next()).isEqualTo(SimpleIdealCaseSuperTypeInterfaceB.class);
    assertThat(supertypeIterator.next()).isEqualTo(SimpleIdealCaseSuperTypeInterfaceAA.class);
    assertThat(supertypeIterator.next()).isEqualTo(SimpleIdealCaseSuperTypeInterfaceAB.class);
    assertThat(supertypeIterator.next()).isEqualTo(SimpleIdealCaseSuperTypeInterfaceShared.class);
    assertThat(supertypeIterator.next()).isEqualTo(SimpleIdealCaseSuperTypeInterfaceAAA.class);
    assertThat(supertypeIterator.next()).isEqualTo(SimpleIdealCaseSuperTypeInterfaceBA.class);
    assertThat(supertypeIterator.hasNext()).isFalse();
  }

  /* package-private */ interface SimpleIdealCaseSuperTypeInterfaceA extends
      SimpleIdealCaseSuperTypeInterfaceAA,
      SimpleIdealCaseSuperTypeInterfaceAB, SimpleIdealCaseSuperTypeInterfaceShared {
  }

  /* package-private */ interface SimpleIdealCaseSuperTypeInterfaceB extends
      SimpleIdealCaseSuperTypeInterfaceBA,
      SimpleIdealCaseSuperTypeInterfaceShared {
  }

  /* package-private */ interface SimpleIdealCaseSuperTypeInterfaceShared {
  }

  /* package-private */ interface SimpleIdealCaseSuperTypeInterfaceAA extends
      SimpleIdealCaseSuperTypeInterfaceAAA {
  }

  /* package-private */ interface SimpleIdealCaseSuperTypeInterfaceAAA {
  }

  /* package-private */ interface SimpleIdealCaseSuperTypeInterfaceAB {
  }

  /* package-private */ interface SimpleIdealCaseSuperTypeInterfaceBA {
  }

  /* package-private */ interface SimpleIdealCaseInterfaceShared<T> extends
      SimpleIdealCaseInterfaceSharedSuper {
  }

  /* package-private */ interface SimpleIdealCaseInterfaceSharedSuper {
  }

  /* package-private */ abstract class SimpleIdealCaseSuperType implements
      SimpleIdealCaseSuperTypeInterfaceA,
      SimpleIdealCaseSuperTypeInterfaceB {
  }

  /* package-private */ class SimpleIdealCaseSubtype<T> extends SimpleIdealCaseSuperType
      implements SimpleIdealCaseInterfaceShared<T> {
  }

  /* package-private */ class SimpleIdealCaseSubSubtype<T> extends SimpleIdealCaseSubtype<T>
      implements SimpleIdealCaseInterfaceShared<T> {
  }
}

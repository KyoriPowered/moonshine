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

package net.kyori.moonshine.strategy.supertype;

import static org.assertj.core.api.Assertions.assertThat;

import io.leangen.geantyref.TypeToken;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StandardSupertypeThenInterfaceSupertypeStrategyTest {
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void simpleSupertypes(final boolean returnObject) {
    final ISupertypeStrategy strategy = new StandardSupertypeThenInterfaceSupertypeStrategy(returnObject);
    final Iterator<Type> supertypeIterator = strategy.hierarchyIterator(SimpleSubtypeB.class);

    assertThat(supertypeIterator.next()).isEqualTo(SimpleSubtypeA.class);
    assertThat(supertypeIterator.next()).isEqualTo(SimpleSupertype.class);
    if (returnObject) {
      assertThat(supertypeIterator.next()).isEqualTo(Object.class);
    }
    assertThat(supertypeIterator.hasNext()).isFalse();
  }

  class SimpleSupertype {
  }

  class SimpleSubtypeA extends SimpleSupertype {
  }

  class SimpleSubtypeB extends SimpleSubtypeA {
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void simpleIdealCase(final boolean returnObject) {
    final ISupertypeStrategy strategy = new StandardSupertypeThenInterfaceSupertypeStrategy(returnObject);
    final Iterator<Type> supertypeIterator = strategy
        .hierarchyIterator(new TypeToken<SimpleIdealCaseSubSubtype<List<Integer>>>() {
        }.getType());

    assertThat(supertypeIterator.next()).isEqualTo(new TypeToken<SimpleIdealCaseSubtype<List<Integer>>>() {
    }.getType());
    assertThat(supertypeIterator.next()).isEqualTo(SimpleIdealCaseSuperType.class);
    if (returnObject) {
      assertThat(supertypeIterator.next()).isEqualTo(Object.class);
    }
    assertThat(supertypeIterator.next()).isEqualTo(new TypeToken<SimpleIdealCaseInterfaceShared<List<Integer>>>() {
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

  interface SimpleIdealCaseSuperTypeInterfaceA extends SimpleIdealCaseSuperTypeInterfaceAA,
      SimpleIdealCaseSuperTypeInterfaceAB, SimpleIdealCaseSuperTypeInterfaceShared {
  }

  interface SimpleIdealCaseSuperTypeInterfaceB extends SimpleIdealCaseSuperTypeInterfaceBA,
      SimpleIdealCaseSuperTypeInterfaceShared {
  }

  interface SimpleIdealCaseSuperTypeInterfaceShared {
  }

  interface SimpleIdealCaseSuperTypeInterfaceAA extends SimpleIdealCaseSuperTypeInterfaceAAA {
  }

  interface SimpleIdealCaseSuperTypeInterfaceAAA {
  }

  interface SimpleIdealCaseSuperTypeInterfaceAB {
  }

  interface SimpleIdealCaseSuperTypeInterfaceBA {
  }

  interface SimpleIdealCaseInterfaceShared<T> extends SimpleIdealCaseInterfaceSharedSuper {
  }

  interface SimpleIdealCaseInterfaceSharedSuper {
  }

  abstract class SimpleIdealCaseSuperType implements SimpleIdealCaseSuperTypeInterfaceA,
      SimpleIdealCaseSuperTypeInterfaceB {
  }

  class SimpleIdealCaseSubtype<T> extends SimpleIdealCaseSuperType
      implements SimpleIdealCaseInterfaceShared<T> {
  }

  class SimpleIdealCaseSubSubtype<T> extends SimpleIdealCaseSubtype<T>
      implements SimpleIdealCaseInterfaceShared<T> {
  }
}

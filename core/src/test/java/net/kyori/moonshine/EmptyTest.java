package net.kyori.moonshine;

import static net.kyori.moonshine.util.Unit.UNIT;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.leangen.geantyref.TypeToken;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EmptyTest {
  @Test
  void emptyTest() {
    assertThatCode(() ->
        Moonshine.builder(TypeToken.get(EmptyDefinition.class))
            .sourced((receiver, key) -> UNIT)
            .rendered((receiver, intermediateMessage, resolvedPlaceholders, method, owner) -> UNIT)
            .sent((receiver, renderedMessage) -> {
            })
            .resolvingWithStrategy((moonshine, receiver, intermediateText, moonshineMethod, parameters) -> Map.of())
            .create()
    ).doesNotThrowAnyException();
  }

  private interface EmptyDefinition {
  }
}

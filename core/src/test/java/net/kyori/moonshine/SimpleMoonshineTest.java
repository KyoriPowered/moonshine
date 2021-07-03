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

package net.kyori.moonshine;

import static java.util.Collections.emptyMap;
import static net.kyori.moonshine.Unit.UNIT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.leangen.geantyref.TypeToken;
import java.util.Map;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.message.IMessageRenderer;
import net.kyori.moonshine.message.IMessageSender;
import net.kyori.moonshine.message.IMessageSource;
import net.kyori.moonshine.model.MoonshineMethod;
import net.kyori.moonshine.strategy.IPlaceholderResolverStrategy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SimpleMoonshineTest {
  @Test
  void emptyMoonshineInstance() {
    assertThatCode(() ->
        Moonshine.<EmptyMoonshineType, String>builder(TypeToken.get(EmptyMoonshineType.class))
            .receiverLocatorResolver((method, proxy) -> (method1, proxy1, parameters) -> "receiver",
                2)
            .sourced((receiver, messageKey) -> UNIT)
            .rendered(
                (receiver, intermediateMessage, resolvedPlaceholders, method, owner) -> UNIT)
            .sent((receiver, renderedMessage) -> {
            })
            .resolvingWithStrategy(new EmptyResolvingStrategy<>())
            .create()
    ).doesNotThrowAnyException();
  }

  @SuppressWarnings("unchecked")
  @Test
  void singleEmptyMethod() throws Exception {
    final IMessageSource<Unit, Unit> source = mock(IMessageSource.class);
    final IMessageRenderer<Unit, Unit, Unit, Unit> messageRenderer = mock(IMessageRenderer.class);
    final IMessageSender<Unit, Unit> messageSender = mock(IMessageSender.class);
    when(source.messageOf(any(), any())).thenReturn(UNIT);
    when(messageRenderer.render(any(), any(), any(), any(), any())).thenReturn(UNIT);

    assertThatCode(() ->
        Moonshine.<SingleEmptyMethodMoonshineType, Unit>builder(
            TypeToken.get(SingleEmptyMethodMoonshineType.class))
            .receiverLocatorResolver((method, proxy) -> (method1, proxy1, parameters) -> UNIT,
                2)
            .sourced(source)
            .rendered(messageRenderer)
            .sent(messageSender)
            .resolvingWithStrategy(new EmptyResolvingStrategy<>())
            .create()
            .method()
    ).doesNotThrowAnyException();
  }

  interface EmptyMoonshineType {
  }

  interface SingleEmptyMethodMoonshineType {
    @Message("test")
    void method();
  }

  private static class EmptyResolvingStrategy<R, I, F> implements
      IPlaceholderResolverStrategy<R, I, F> {
    @Override
    public Map<String, ? extends F> resolvePlaceholders(final Moonshine<R, I, ?, F> moonshine,
        final R receiver, final I intermediateText,
        final MoonshineMethod<? extends R> moonshineMethod,
        final @Nullable Object[] parameters) {
      return emptyMap();
    }
  }
}

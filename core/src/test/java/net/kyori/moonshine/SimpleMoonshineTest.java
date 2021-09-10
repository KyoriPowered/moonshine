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
package net.kyori.moonshine;

import static java.util.Collections.emptyMap;
import static net.kyori.moonshine.util.Unit.UNIT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.leangen.geantyref.TypeToken;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.Placeholder;
import net.kyori.moonshine.message.IMessageRenderer;
import net.kyori.moonshine.message.IMessageSender;
import net.kyori.moonshine.message.IMessageSource;
import net.kyori.moonshine.model.MoonshineMethod;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.strategy.IPlaceholderResolverStrategy;
import net.kyori.moonshine.strategy.StandardPlaceholderResolverStrategy;
import net.kyori.moonshine.strategy.supertype.StandardSupertypeThenInterfaceSupertypeStrategy;
import net.kyori.moonshine.util.Either;
import net.kyori.moonshine.util.Unit;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

/* package-private */ class SimpleMoonshineTest {
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
    final IMessageSource<Unit, Unit> messageSource = mock(IMessageSource.class);
    final IMessageRenderer<Unit, Unit, Unit, Unit> messageRenderer = mock(IMessageRenderer.class);
    final IMessageSender<Unit, Unit> messageSender = mock(IMessageSender.class);
    when(messageSource.messageOf(any(), any())).thenReturn(UNIT);
    when(messageRenderer.render(any(), any(), any(), any(), any())).thenReturn(UNIT);

    assertThatCode(() ->
        Moonshine.<SingleEmptyMethodMoonshineType, Unit>builder(
                TypeToken.get(SingleEmptyMethodMoonshineType.class))
            .receiverLocatorResolver((method, proxy) -> (method1, proxy1, parameters) -> UNIT,
                2)
            .sourced(messageSource)
            .rendered(messageRenderer)
            .sent(messageSender)
            .resolvingWithStrategy(new EmptyResolvingStrategy<>())
            .create()
            .method()
    ).doesNotThrowAnyException();
  }

  @SuppressWarnings("unchecked")
  @Test
  void singleMethodStringPlaceholders() throws Exception {
    final IMessageSource<TestableReceiver, String> messageSource = mock(IMessageSource.class);
    final IMessageRenderer<TestableReceiver, String, String, String> messageRenderer = spy(
        new SimpleStringFormatRenderer<>());
    final IMessageSender<TestableReceiver, String> messageSender = mock(IMessageSender.class);
    final TestableReceiver receiver = mock(TestableReceiver.class);
    when(messageSource.messageOf(any(), any())).thenReturn("Hello, %2$s!");

    assertThatCode(() ->
        Moonshine.<SingleMethodStringPlaceholdersMoonshineType, TestableReceiver>builder(
                TypeToken.get(SingleMethodStringPlaceholdersMoonshineType.class))
            .receiverLocatorResolver((method, proxy) -> (method1, proxy1, parameters) -> receiver,
                -1)
            .sourced(messageSource)
            .rendered(messageRenderer)
            .sent(messageSender)
            .resolvingWithStrategy(new StandardPlaceholderResolverStrategy<>(
                new StandardSupertypeThenInterfaceSupertypeStrategy(false)))
            .weightedPlaceholderResolver(String.class,
                (placeholderName, value, receiver1, owner, method, parameters) -> null,
                3)
            .weightedPlaceholderResolver(String.class,
                (placeholderName, value, receiver1, owner, method, parameters) ->
                    Map.of(placeholderName, Either.left(ConclusionValue.conclusionValue(value))),
                1)
            .weightedPlaceholderResolver(TypeToken.get(StringPlaceholderValue.class),
                (placeholderName, value, receiver1, owner, method, parameters) ->
                    Map.of(placeholderName, Either.right(
                        ContinuanceValue.continuanceValue(value.value(), String.class))),
                1)
            .create()
            .method(receiver, "first", new SimpleStringPlaceholder("second"))
    ).doesNotThrowAnyException();

    verify(messageSource).messageOf(receiver, "test");
    verify(messageRenderer).render(receiver, "Hello, %2$s!",
        new LinkedHashMap<>(Map.of("placeholder", "first", "cringe", "second")),
        SingleMethodStringPlaceholdersMoonshineType.class.getMethods()[0],
        TypeToken.get(SingleMethodStringPlaceholdersMoonshineType.class).getType());
    verify(messageSender).send(receiver, "Hello, second!");
  }

  @Test
  void defaultMethodOneParam() throws Exception {
    final IMessageSource<TestableReceiver, String> messageSource = mock(IMessageSource.class);
    final IMessageRenderer<TestableReceiver, String, String, String> messageRenderer = spy(
        new SimpleStringFormatRenderer<>());
    final IMessageSender<TestableReceiver, String> messageSender = mock(IMessageSender.class);
    final TestableReceiver receiver = mock(TestableReceiver.class);
    when(messageSource.messageOf(any(), any())).thenReturn("Hello, %1$s!");

    assertThatCode(() ->
        Moonshine.<DefaultMethodNoParams, TestableReceiver>builder(TypeToken.get(DefaultMethodNoParams.class))
            .receiverLocatorResolver((method, proxy) -> (method1, proxy1, parameters) -> receiver, -1)
            .sourced(messageSource)
            .rendered(messageRenderer)
            .sent(messageSender)
            .resolvingWithStrategy(new StandardPlaceholderResolverStrategy<>(
                new StandardSupertypeThenInterfaceSupertypeStrategy(false)
            ))
            .weightedPlaceholderResolver(String.class,
                (placeholderName, value, receiver1, owner, method, parameters) ->
                    Map.of(placeholderName, Either.left(ConclusionValue.conclusionValue(value))), 1)
            .create()
            .method(receiver)
    ).doesNotThrowAnyException();

    verify(messageSource).messageOf(receiver, "test");
    verify(messageRenderer).render(receiver, "Hello, %1$s!",
        new LinkedHashMap<>(Map.of("placeholder", "placeholder value")),
        DefaultMethodNoParams.class.getMethods()[0],
        TypeToken.get(DefaultMethodNoParams.class).getType());
    verify(messageSender).send(receiver, "Hello, placeholder value!");
  }

  interface EmptyMoonshineType {
  }

  interface SingleEmptyMethodMoonshineType {
    @Message("test")
    void method();
  }

  interface SingleMethodStringPlaceholdersMoonshineType {
    @Message("test")
    void method(
        final TestableReceiver receiver,
        @Placeholder final String placeholder,
        @Placeholder("cringe") final SimpleStringPlaceholder placeholder2
    );
  }

  interface DefaultMethodNoParams {
    @Message("test")
    void method(
        final TestableReceiver receiver,
        @Placeholder final String placeholder
    );

    default void method(final TestableReceiver receiver) {
      this.method(receiver, "placeholder value");
    }
  }

  private static class TestableReceiver {
    void send(final Object message) {
      fail("TestableReceiver#send must be mocked");
    }
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

  private static class SimpleStringFormatRenderer<R> implements
      IMessageRenderer<R, String, String, String> {
    @Override
    public String render(final R receiver, final String intermediateMessage,
        final Map<String, ? extends String> resolvedPlaceholders, final Method method,
        final Type owner) {
      return String.format(intermediateMessage, resolvedPlaceholders.values().toArray());
    }
  }

  private interface StringPlaceholderValue {
    String value();
  }

  private static class SimpleStringPlaceholder implements StringPlaceholderValue {
    private final String value;

    private SimpleStringPlaceholder(final String value) {
      this.value = value;
    }

    @Override
    public String value() {
      return this.value;
    }
  }
}

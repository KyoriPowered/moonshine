package net.kyori.moonshine;

import static net.kyori.moonshine.util.Unit.UNIT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.leangen.geantyref.TypeToken;
import java.util.Map;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.Placeholder;
import net.kyori.moonshine.message.IMessageRenderer;
import net.kyori.moonshine.message.IMessageSender;
import net.kyori.moonshine.message.IMessageSource;
import net.kyori.moonshine.strategy.StandardPlaceholderResolverStrategy;
import net.kyori.moonshine.strategy.supertype.StandardSupertypeThenInterfaceSupertypeStrategy;
import net.kyori.moonshine.util.Unit;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class DefaultMethodTest {
  private static final String MESSAGE_KEY = "test";
  private static final String DEFAULT_VALUE = "default placeholder value";

  @Test
  void emptyDefaultMethodTest() throws Exception {
    final IMessageSource<Unit, Unit> source = mock(IMessageSource.class);
    final IMessageRenderer<Unit, Unit, Unit, Unit> renderer = mock(IMessageRenderer.class);
    final IMessageSender<Unit, Unit> sender = mock(IMessageSender.class);
    when(source.messageOf(any(), any())).thenReturn(UNIT);
    when(renderer.render(any(), any(), any(), any(), any())).thenReturn(UNIT);

    assertThatCode(() ->
        Moonshine.<TestType, Unit>builder(TypeToken.get(TestType.class))
            .receiverLocatorResolver((method, proxy) -> (method1, proxy1, parameters) -> UNIT, 1)
            .sourced(source)
            .rendered(renderer)
            .sent(sender)
            .resolvingWithStrategy(new StandardPlaceholderResolverStrategy<>(
                new StandardSupertypeThenInterfaceSupertypeStrategy(false)
            ))
            .weightedPlaceholderResolver(String.class,
                (placeholderName, value, receiver, owner, method, parameters) -> Map.of(), 1)
            .create()
            .empty()
    ).doesNotThrowAnyException();

    verify(source).messageOf(UNIT, MESSAGE_KEY);
    verify(sender).send(UNIT, UNIT);
  }

  @Test
  void defaultMethodTest() throws Exception {
    final IMessageSource<Unit, Unit> source = mock(IMessageSource.class);
    final IMessageRenderer<Unit, Unit, Unit, Unit> renderer = mock(IMessageRenderer.class);
    final IMessageSender<Unit, Unit> sender = mock(IMessageSender.class);
    when(source.messageOf(any(), any())).thenReturn(UNIT);
    when(renderer.render(any(), any(), any(), any(), any())).thenReturn(UNIT);

    assertThatCode(() ->
        Moonshine.<TestType, Unit>builder(TypeToken.get(TestType.class))
            .receiverLocatorResolver((method, proxy) -> (method1, proxy1, parameters) -> UNIT, 1)
            .sourced(source)
            .rendered(renderer)
            .sent(sender)
            .resolvingWithStrategy(new StandardPlaceholderResolverStrategy<>(
                new StandardSupertypeThenInterfaceSupertypeStrategy(false)
            ))
            .weightedPlaceholderResolver(String.class,
                (placeholderName, value, receiver, owner, method, parameters) -> Map.of(), 1)
            .create()
            .withParameter(DEFAULT_VALUE)
    ).doesNotThrowAnyException();

    verify(source).messageOf(UNIT, MESSAGE_KEY);
    verify(sender).send(UNIT, UNIT);
  }

  interface TestType {
    @Message(MESSAGE_KEY)
    void method(@Placeholder final String placeholder);

    default void empty() {
      this.method(DEFAULT_VALUE);
    }

    default void withParameter(final String placeholder) {
      this.method(placeholder);
    }
  }
}

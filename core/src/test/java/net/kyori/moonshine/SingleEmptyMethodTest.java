package net.kyori.moonshine;

import static net.kyori.moonshine.util.Unit.UNIT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.leangen.geantyref.TypeToken;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.message.IMessageRenderer;
import net.kyori.moonshine.message.IMessageSender;
import net.kyori.moonshine.message.IMessageSource;
import net.kyori.moonshine.strategy.StandardPlaceholderResolverStrategy;
import net.kyori.moonshine.strategy.supertype.StandardSupertypeThenInterfaceSupertypeStrategy;
import net.kyori.moonshine.util.Unit;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class SingleEmptyMethodTest {
  private static final String MESSAGE_KEY = "test";

  @Test
  void simpleEmptyMethodMoonshineTest() throws Exception {
    final IMessageSource<Unit, Unit> source = mock(IMessageSource.class);
    final IMessageRenderer<Unit, Unit, Unit, Unit> renderer = mock(IMessageRenderer.class);
    final IMessageSender<Unit, Unit> sender = mock(IMessageSender.class);
    when(source.messageOf(any(), any())).thenReturn(UNIT);
    when(renderer.render(any(), any(), any(), any(), any())).thenReturn(UNIT);

    assertThatCode(() ->
        Moonshine.<SimpleType, Unit>builder(TypeToken.get(SimpleType.class))
            .receiverLocatorResolver((method, proxy) -> (method1, proxy1, parameters) -> UNIT, 1)
            .sourced(source)
            .rendered(renderer)
            .sent(sender)
            .resolvingWithStrategy(new StandardPlaceholderResolverStrategy<>(
                new StandardSupertypeThenInterfaceSupertypeStrategy(false)
            ))
            .create()
            .method()
    ).doesNotThrowAnyException();

    verify(source).messageOf(UNIT, MESSAGE_KEY);
    verify(sender).send(UNIT, UNIT);
  }

  interface SimpleType {
    @Message(MESSAGE_KEY)
    void method();
  }
}

package net.kyori.moonshine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.leangen.geantyref.TypeToken;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.MessageSection;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.message.IMessageSource;
import net.kyori.moonshine.strategy.StandardPlaceholderResolverStrategy;
import net.kyori.moonshine.strategy.supertype.StandardSupertypeThenInterfaceSupertypeStrategy;
import net.kyori.moonshine.util.Unit;
import org.junit.jupiter.api.Test;

class NestedMoonshineTest {
    @Test
    void simpleNestedMoonshine() throws UnscannableMethodException {
        SimpleNestedMoonshineRoot root = moonshineWith(SimpleNestedMoonshineRoot.class, (receiver, messageKey) -> {
            if ("test".equals(messageKey)) {
                return "ok";
            }
            return null;
        });
        assertEquals("ok", root.child().test());
    }

    @Test
    void complexNestedMoonshine() throws UnscannableMethodException {
        ComplexNestedMoonshineRoot root = moonshineWith(ComplexNestedMoonshineRoot.class, (receiver, messageKey) -> {
            if ("complex.child.child-test".equals(messageKey)) {
                return "ok";
            }
            if ("complex.child.child-oke-ok.done".equals(messageKey)) {
                return "yes";
            }
            return null;
        });
        assertEquals("ok", root.child().test());
        assertEquals("yes", root.child().oke().done());
    }

    private <T> T moonshineWith(Class<T> type, IMessageSource<Unit, ?> sourced) throws UnscannableMethodException {
        return Moonshine.<T, Unit>builder(TypeToken.get(type))
                .receiverLocatorResolver((method, proxy) -> (method1, proxy1, arguments) -> null, 1)
                .sourced(sourced)
                .rendered((receiver, intermediateMessage, resolvedPlaceholders, method, owner) -> intermediateMessage)
                .sent((receiver, renderedMessage) -> {})
                .resolvingWithStrategy(new StandardPlaceholderResolverStrategy<>(new StandardSupertypeThenInterfaceSupertypeStrategy(false)))
                .create();
    }

    interface SimpleNestedMoonshineRoot {
        SimpleNestedMoonshineChild child();

        @MessageSection
        interface SimpleNestedMoonshineChild {
            @Message("test")
            String test();
        }
    }

    @MessageSection("complex")
    interface ComplexNestedMoonshineRoot {
        @Message("child")
        ComplexNestedMoonshineChild child();

        @MessageSection(value = "child", delimiter = '-')
        interface ComplexNestedMoonshineChild {
            @Message("test")
            String test();

            @Message("oke")
            ComplexNestedMoonshineChildChild oke();

            @MessageSection("ok")
            interface ComplexNestedMoonshineChildChild {
                @Message("done")
                String done();
            }
        }
    }
}

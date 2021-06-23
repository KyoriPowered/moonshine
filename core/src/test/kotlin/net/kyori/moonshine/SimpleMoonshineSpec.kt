/**
 * moonshine - A localisation library for Java.
 * Copyright (C) 2021 Mariell Hoversholm
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
package net.kyori.moonshine

import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.kyori.moonshine.annotation.Message
import net.kyori.moonshine.annotation.Placeholder
import net.kyori.moonshine.ext.typeToken
import net.kyori.moonshine.placeholder.ConclusionValue
import net.kyori.moonshine.receiver.IReceiverLocator
import net.kyori.moonshine.strategy.StandardPlaceholderResolverStrategy
import net.kyori.moonshine.strategy.supertype.StandardSupertypeThenInterfaceSupertypeStrategy
import net.kyori.moonshine.util.Either

class SimpleMoonshineSpec : StringSpec({
    "no methods defined" {
        Moonshine.builder<EmptyMoonshineInterface, String>(typeToken())
            .sourced { _, _ -> }
            .rendered<Unit, String> { _, _, _, _, _ -> }
            .sent { _, _ -> }
            .resolvingWithStrategy { _, _, _, _, _ -> emptyMap() }
            .create()
    }

    "single, empty method" {
        Moonshine.builder<SingleMethod, String>(typeToken())
            .receiverLocatorResolver({ _, _ -> IReceiverLocator { _, _, _ -> "test method" } }, 1)
            .sourced { _, _ -> }
            .rendered<Unit, String> { _, _, _, _, _ -> }
            .sent { _, _ -> }
            .resolvingWithStrategy { _, _, _, _, _ -> emptyMap() }
            .create()
            .method()
    }

    "single, fully fledged method" {
        val receiver = mockk<TestableReceiver<String>>()
        every { receiver.send(any()) } returns Unit

        Moonshine.builder<SingleFullyFledgedMethod, TestableReceiver<String>>(typeToken())
            .receiverLocatorResolver({ _, _ -> IReceiverLocator { _, _, _ -> receiver } }, 1)
            .sourced { recv, name ->
                recv shouldBeSameInstanceAs receiver
                name shouldBe "test"
                "Hello, %placeholder2ButCool%!"
            }
            .rendered<String, String> { _, msg, placeholders, _, _ ->
                placeholders.entries.fold(msg) { acc, (key, value) ->
                    acc.replace("%$key%", value)
                }
            }
            .sent(TestableReceiver<String>::send)
            .resolvingWithStrategy(
                StandardPlaceholderResolverStrategy(
                    StandardSupertypeThenInterfaceSupertypeStrategy(
                        true
                    )
                )
            )
            .weightedPlaceholderResolver(
                typeToken<String>(),
                { placeholderName, value, receiver, owner, method, parameters ->
                    mapOf(
                        placeholderName to Either.left(ConclusionValue.conclusionValue(value.uppercase())),
                        "${placeholderName}ButCool" to Either.left(ConclusionValue.conclusionValue(value.repeat(value.length)))
                    )
                },
                3
            )
            .create()
            .method(receiver, "World", "bromcc")

        verify(exactly = 1) { receiver.send("Hello, ${"bromcc".repeat(6)}!") }
    }
}) {
    interface EmptyMoonshineInterface

    interface SingleMethod {
        @Message("test")
        fun method()
    }

    interface SingleFullyFledgedMethod {
        @Message("test")
        fun method(
            receiver: TestableReceiver<String>,
            @Placeholder placeholder: String,
            @Placeholder placeholder2: String
        )
    }

    class TestableReceiver<T> {
        fun send(message: T) {
            fail("this must be mocked")
        }
    }
}

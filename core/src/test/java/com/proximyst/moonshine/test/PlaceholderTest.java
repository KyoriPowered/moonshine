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

package com.proximyst.moonshine.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.proximyst.moonshine.Moonshine;
import com.proximyst.moonshine.annotation.Flag;
import com.proximyst.moonshine.annotation.Message;
import com.proximyst.moonshine.annotation.Placeholder;
import com.proximyst.moonshine.annotation.Receiver;
import com.proximyst.moonshine.message.IMessageParser;
import com.proximyst.moonshine.message.IMessageSender;
import com.proximyst.moonshine.message.IMessageSource;
import com.proximyst.moonshine.util.StringReplaceMessageParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceholderTest {
  private static final IMessageParser<String, String, String> MESSAGE_PARSER = new StringReplaceMessageParser<>();

  @Mock
  private IMessageSource<String> messageSource;

  @Mock
  private IMessageSender<String, String> messageSender;

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void simplePlaceholder(final boolean uppercase) {
    when(this.messageSource.message(any())).thenReturn("abc %placeholder%");
    final TestMessages testMessages = Moonshine.<String>builder()
        .source(this.messageSource)
        .parser(MESSAGE_PARSER)
        .sender(this.messageSender)
        .create(TestMessages.class);

    // Ensure the placeholder has a little of both cases first.
    final String placeholder = RandomStringUtils.randomAlphabetic(8).toLowerCase()
        + RandomStringUtils.randomAlphabetic(8).toUpperCase();
    final String expected = uppercase ? placeholder.toUpperCase() : placeholder;

    testMessages.test("testreceiver", placeholder, uppercase);

    verify(this.messageSource).message(eq("flaggedplaceholder"));
    verify(this.messageSender)
        .sendMessage(eq("testreceiver"), eq("abc " + expected));
  }

  interface TestMessages {
    @Message("flaggedplaceholder")
    void test(final @Receiver Object receiver,

        @Placeholder(flags = "uppercase")
            String placeholder,

        @Flag(type = String.class, name = "uppercase")
        @Flag(type = boolean.class, name = "cool") final boolean toUpperCase);
  }
}

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
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceholderTest {
  private static final String MESSAGE = "abc %placeholder%";
  private static final IMessageParser<String, String, String> MESSAGE_PARSER = new StringReplaceMessageParser<>();

  @Mock
  private IMessageSource<String, String> messageSource;

  @Mock
  private IMessageSender<String, String> messageSender;

  private TestMessages testMessages;

  @BeforeEach
  void setUp() {
    when(this.messageSource.message(any(), any())).thenReturn(MESSAGE);
    this.testMessages = Moonshine.<String>builder()
        .source(this.messageSource)
        .parser(MESSAGE_PARSER)
        .sender(this.messageSender)
        .create(TestMessages.class);
  }

  @Test
  void simple() {
    final long placeholder = new Random().nextLong();

    this.testMessages.simple("receiver", placeholder);

    verify(this.messageSource).message("simpleplaceholder", "receiver");
    verify(this.messageSender).sendMessage("receiver", "abc " + placeholder);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void flags(final boolean uppercase) {
    // Ensure the placeholder has a little of both cases first.
    final String placeholder = RandomStringUtils.randomAlphabetic(8).toLowerCase()
        + RandomStringUtils.randomAlphabetic(8).toUpperCase();
    final String expected = uppercase ? placeholder.toUpperCase() : placeholder;

    this.testMessages.flagged("testreceiver", placeholder, uppercase);

    verify(this.messageSource).message("flaggedplaceholder", "testreceiver");
    verify(this.messageSender).sendMessage("testreceiver", "abc " + expected);
  }

  @Test
  void flatNumberDecimal() {
    final short num = (short) (new Random().nextInt() % Short.MAX_VALUE);
    this.testMessages.flatNumber("receiver", num, false, false, false);

    verify(this.messageSource).message("number", "receiver");
    verify(this.messageSender).sendMessage("receiver", "abc " + num);
  }

  @Test
  void flatNumberHexadecimal() {
    final long num = new Random().nextLong();
    this.testMessages.flatNumber("receiver", num, true, false, true);

    verify(this.messageSource).message("number", "receiver");
    verify(this.messageSender).sendMessage("receiver", "abc " + Long.toHexString(-num));
  }

  @Test
  void flatNumberOctal() {
    final int num = new Random().nextInt();
    this.testMessages.flatNumber("receiver", num, false, true, false);

    verify(this.messageSource).message("number", "receiver");
    verify(this.messageSender).sendMessage("receiver", "abc " + Long.toOctalString(num));
  }

  @Test
  void floatingDecimal() {
    final float num = new Random().nextFloat();
    this.testMessages.floatingNumber("receiver", num, false, true);

    verify(this.messageSource).message("number", "receiver");
    verify(this.messageSender).sendMessage("receiver", "abc " + Double.toString(-num));
  }

  @Test
  void floatingHexadecimal() {
    final double num = new Random().nextDouble();
    this.testMessages.floatingNumber("receiver", num, true, false);

    verify(this.messageSource).message("number", "receiver");
    verify(this.messageSender).sendMessage("receiver", "abc " + Double.toHexString(num));
  }

  interface TestMessages {
    @Message("simpleplaceholder")
    void simple(final @Receiver Object receiver,
        @Placeholder final Long placeholder);

    @Message("flaggedplaceholder")
    void flagged(final @Receiver Object receiver,

        @Placeholder(flags = "uppercase") final String placeholder,

        @Flag(type = String.class, name = "uppercase")
        @Flag(type = boolean.class, name = "cool") final boolean toUpperCase);

    @Message("number")
    void flatNumber(final @Receiver Object receiver,

        @Placeholder(flags = {"hexadecimal", "octal", "flip"}) final Number placeholder,

        @Flag(type = Number.class, name = "hexadecimal") final boolean hex,
        @Flag(type = Number.class, name = "octal") final boolean octal,
        @Flag(type = Number.class, name = "flip") final boolean flipSign);

    @Message("number")
    void floatingNumber(final @Receiver Object receiver,

        @Placeholder(flags = {"hexadecimal", "octal", "flip"}) final Number placeholder,

        @Flag(type = Number.class, name = "hexadecimal") final boolean hex,
        @Flag(type = Number.class, name = "flip") final boolean flipSign);
  }
}

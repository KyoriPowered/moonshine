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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.proximyst.moonshine.Moonshine;
import com.proximyst.moonshine.annotation.Message;
import com.proximyst.moonshine.annotation.Receiver;
import com.proximyst.moonshine.message.IMessageParser;
import com.proximyst.moonshine.message.IMessageSender;
import com.proximyst.moonshine.message.IMessageSource;
import com.proximyst.moonshine.util.StringReplaceMessageParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SimpleMessageTest {
  private static final IMessageParser<String, String, String> MESSAGE_PARSER = new StringReplaceMessageParser<>();

  @Mock
  private IMessageSource<String> messageSource;

  @Mock
  private IMessageSender<String, String> messageSender;

  @Test
  void validMessage() {
    final String message = "Cool, simple message";
    when(this.messageSource.message(any())).thenReturn(message);
    final TestMessages testMessages = Moonshine.<String>builder()
        .source(this.messageSource)
        .parser(MESSAGE_PARSER)
        .sender(this.messageSender)
        .create(TestMessages.class);

    testMessages.test("receiver");

    verify(this.messageSource).message("message");
    verify(this.messageSender).sendMessage("receiver", message);
  }

  @Test
  void invalidMessage() {
    final TestMessages testMessages = Moonshine.<String>builder()
        .source(this.messageSource)
        .parser(MESSAGE_PARSER)
        .sender(this.messageSender)
        .create(TestMessages.class);
    assertThatThrownBy(() -> testMessages.test("receiver"))
        .isInstanceOf(IllegalStateException.class);
  }

  interface TestMessages {
    @Message("message")
    void test(final @Receiver Object receiver);
  }
}

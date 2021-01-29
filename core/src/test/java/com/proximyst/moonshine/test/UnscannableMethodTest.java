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

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.proximyst.moonshine.Moonshine;
import com.proximyst.moonshine.annotation.Message;
import com.proximyst.moonshine.annotation.Placeholder;
import com.proximyst.moonshine.annotation.Receiver;
import com.proximyst.moonshine.exception.UnscannableMethodException;
import com.proximyst.moonshine.message.IMessageParser;
import com.proximyst.moonshine.message.IMessageSender;
import com.proximyst.moonshine.message.IMessageSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnscannableMethodTest {
  @Mock
  private IMessageParser<Object, Object, Object> messageParser;

  @Mock
  private IMessageSender<Object, Object> messageSender;

  @Mock
  private IMessageSource<Object> messageSource;

  @Test
  void noMessageKey() {
    assertThrows(UnscannableMethodException.class, () -> Moonshine.builder()
        .source(this.messageSource)
        .parser(this.messageParser)
        .sender(this.messageSender)
        .create(NoMessageKey.class));
  }

  @Test
  void noReceiver() {
    assertThrows(UnscannableMethodException.class, () -> Moonshine.builder()
        .source(this.messageSource)
        .parser(this.messageParser)
        .sender(this.messageSender)
        .create(NoReceiver.class));
  }

  @Test
  void missingPlaceholderResolver() {
    assertThrows(UnscannableMethodException.class, () -> Moonshine.builder()
        .source(this.messageSource)
        .parser(this.messageParser)
        .sender(this.messageSender)
        .create(MissingPlaceholderResolver.class));
  }

  interface NoMessageKey {
    void test(final @Receiver Object receiver);
  }

  interface NoReceiver {
    @Message("test")
    void test();
  }

  interface MissingPlaceholderResolver {
    @Message("test")
    void test(final @Receiver Object receiver,

        @Placeholder("you're my little pogchamp") final Void placeholder);
  }
}

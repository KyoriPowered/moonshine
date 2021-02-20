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

import static org.mockito.Mockito.mock;

import com.proximyst.moonshine.Moonshine;
import com.proximyst.moonshine.annotation.Flag;
import com.proximyst.moonshine.annotation.Message;
import com.proximyst.moonshine.annotation.Placeholder;
import com.proximyst.moonshine.annotation.Receiver;
import com.proximyst.moonshine.component.placeholder.IPlaceholderResolver;
import com.proximyst.moonshine.component.receiver.IReceiverResolver;
import com.proximyst.moonshine.message.IMessageParser;
import com.proximyst.moonshine.message.IMessageSender;
import com.proximyst.moonshine.message.IMessageSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScanningTest {
  @Mock
  private IMessageSource<Object, Object> messageSource;

  @Mock
  private IMessageSender<Object, Object> messageSender;

  @Mock
  private IMessageParser<Object, Object, Object> messageParser;

  @SuppressWarnings("unchecked")
  @Test
  void scan() {
    Moonshine.builder()
        .placeholder(Object.class, mock(IPlaceholderResolver.class))
        .receiver(mock(IReceiverResolver.class))
        .source(mock(IMessageSource.class))
        .parser(mock(IMessageParser.class))
        .sender(mock(IMessageSender.class))
        .create(TestMessages.class);
  }

  interface TestMessages {
    static void staticIsIgnored() {
    }

    default void defaultIsIgnored() {
    }

    @Message("test")
    void test(final @Receiver Object receiver,
        @Placeholder final Object placeholder,
        @Flag(type = Object.class, name = "flag") final Object flag);
  }
}

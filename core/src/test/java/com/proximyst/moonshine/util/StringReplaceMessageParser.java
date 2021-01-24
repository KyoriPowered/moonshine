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

package com.proximyst.moonshine.util;

import com.proximyst.moonshine.message.IMessageParser;
import com.proximyst.moonshine.message.ParsingContext;
import java.util.Map.Entry;

public class StringReplaceMessageParser<R> implements IMessageParser<String, String, R> {
  @Override
  public String parse(String message, final ParsingContext<R> parsingContext) {
    for (final Entry<String, String> entry : parsingContext.placeholders().entrySet()) {
      message = message.replace('%' + entry.getKey() + '%', entry.getValue());
    }
    return message;
  }
}

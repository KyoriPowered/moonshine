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

package com.proximyst.moonshine.component.receiver;

import java.lang.reflect.Method;

public final class ReceiverContext {
  private final Method method;
  private final Object proxy;
  private final Object[] parameters;

  public ReceiverContext(final Method method, final Object proxy, final Object[] parameters) {
    this.method = method;
    this.proxy = proxy;
    this.parameters = parameters;
  }

  public Method method() {
    return this.method;
  }

  public Object proxy() {
    return this.proxy;
  }

  public Object[] parameters() {
    return this.parameters;
  }
}

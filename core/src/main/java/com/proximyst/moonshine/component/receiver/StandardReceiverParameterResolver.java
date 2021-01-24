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
import java.lang.reflect.Parameter;
import java.util.Optional;
import com.proximyst.moonshine.annotation.Receiver;

public final class StandardReceiverParameterResolver<R> implements IReceiverResolver<R> {
  @Override
  public Optional<IReceiver<R>> resolve(final Method method) {
    if (method.getParameterCount() == 0) {
      return Optional.empty();
    }

    final Parameter[] parameters = method.getParameters();
    for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
      final Parameter parameter = parameters[i];
      if (parameter.isAnnotationPresent(Receiver.class)) {
        return Optional.of(new StandardReceiverByParameter<>(i));
      }
    }

    return Optional.empty();
  }

  private static final class StandardReceiverByParameter<R> implements IReceiver<R> {
    private final int parameterIdx;

    private StandardReceiverByParameter(final int parameterIdx) {
      this.parameterIdx = parameterIdx;
    }

    // This cast should be safe. If the receiver type is unexpected, this is on the user as we can't check it
    // without adding considerable debt to the usage code.
    @SuppressWarnings("unchecked")
    @Override
    public R find(final ReceiverContext ctx) {
      return (R) ctx.parameters()[this.parameterIdx];
    }
  }
}

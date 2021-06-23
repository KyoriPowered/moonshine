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

package net.kyori.moonshine.strategy;

import com.google.common.collect.Iterators;
import io.leangen.geantyref.GenericTypeReflector;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import javax.annotation.concurrent.ThreadSafe;
import net.kyori.moonshine.Moonshine;
import net.kyori.moonshine.annotation.Placeholder;
import net.kyori.moonshine.exception.PlaceholderResolvingException;
import net.kyori.moonshine.exception.UnfinishedPlaceholderException;
import net.kyori.moonshine.model.MoonshineMethod;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.placeholder.IPlaceholderResolver;
import net.kyori.moonshine.strategy.supertype.ISupertypeStrategy;
import net.kyori.moonshine.util.Either;
import net.kyori.moonshine.util.Weighted;
import org.checkerframework.checker.nullness.qual.Nullable;

@ThreadSafe
public final class StandardPlaceholderResolverStrategy<R, I, F> implements IPlaceholderResolverStrategy<R, I, F> {
  private final ISupertypeStrategy supertypeStrategy;

  public StandardPlaceholderResolverStrategy(final ISupertypeStrategy supertypeStrategy) {
    this.supertypeStrategy = supertypeStrategy;
  }

  @Override
  public Map<String, ? extends F> resolvePlaceholders(final Moonshine<R, I, ?, F> moonshine,
      final R receiver, final I intermediateText, final MoonshineMethod<? extends R> moonshineMethod,
      final @Nullable Object[] parameters)
      throws PlaceholderResolvingException {
    if (parameters.length == 0) {
      return Collections.emptyMap();
    }

    final Map<String, F> finalisedPlaceholders = new HashMap<>(parameters.length);
    final Map<String, ContinuanceValue<?>> resolvingPlaceholders = new HashMap<>(16);
    final Parameter[] methodParameters = moonshineMethod.reflectMethod().getParameters();
    final Type[] exactParameterTypes = GenericTypeReflector.getParameterTypes(
        moonshineMethod.reflectMethod(), moonshine.proxiedType().getType());

    for (int idx = 0; idx < parameters.length; ++idx) {
      final Parameter parameter = methodParameters[idx];
      final @Nullable Object value = parameters[idx];
      if (value == null) {
        // Nothing to resolve with.
        continue;
      }

      final Type parameterType = GenericTypeReflector.getExactSubType(
          exactParameterTypes[idx], value.getClass());

      final @Nullable Placeholder placeholder = parameter.getAnnotation(Placeholder.class);
      //noinspection ConstantConditions -- Javadocs say it returns null if absent.
      if (placeholder == null) {
        // Nothing to resolve.
        continue;
      }

      final String placeholderName = placeholder.value().isEmpty()
          ? parameter.getName()
          : placeholder.value();
      resolvingPlaceholders.put(placeholderName, ContinuanceValue.continuanceValue(value, parameterType));
    }

    this.resolvePlaceholder(moonshine, receiver, finalisedPlaceholders,
        resolvingPlaceholders, moonshineMethod, parameters);

    return finalisedPlaceholders;
  }

  /**
   * Resolve a single placeholder.
   *
   * @param moonshine             the moonshine instance
   * @param finalisedPlaceholders the finalised placeholders
   * @param resolvingPlaceholders the placeholders to resolve
   * @param moonshineMethod       the method we are resolving a placeholder for
   */
  private void resolvePlaceholder(final Moonshine<R, I, ?, F> moonshine, final R receiver,
      final Map<String, F> finalisedPlaceholders,
      final Map<String, ContinuanceValue<?>> resolvingPlaceholders,
      final MoonshineMethod<? extends R> moonshineMethod, final @Nullable Object[] parameters)
      throws UnfinishedPlaceholderException {
    final Map<Type, NavigableSet<Weighted<? extends IPlaceholderResolver<? extends R, ?, ? extends F>>>> weightedPlaceholderResolvers =
        moonshine.weightedPlaceholderResolvers();

    dancing:
    // Shamelessly stealing kashike's joke
    while (!resolvingPlaceholders.isEmpty()) {
      final Iterator<Entry<String, ContinuanceValue<?>>> resolvingPlaceholderIterator = resolvingPlaceholders.entrySet().iterator();
      while (resolvingPlaceholderIterator.hasNext()) {
        final Entry<String, ContinuanceValue<?>> continuanceEntry = resolvingPlaceholderIterator.next();
        final String continuancePlaceholderName = continuanceEntry.getKey();
        final Type type = continuanceEntry.getValue().type();
        final Object value = continuanceEntry.getValue().value();

        final Iterator<Type> hierarchyIterator = Iterators.concat(Iterators.singletonIterator(type), this.supertypeStrategy.hierarchyIterator(type));
        while (hierarchyIterator.hasNext()) {
          final Type supertype = hierarchyIterator.next();

          for (final Weighted<? extends IPlaceholderResolver<? extends R, ?, ? extends F>> weighted :
              weightedPlaceholderResolvers.getOrDefault(supertype, Collections.emptyNavigableSet())) {
            @SuppressWarnings("unchecked") // This should be equivalent.
            final IPlaceholderResolver<R, Object, ? extends F> placeholderResolver =
                (IPlaceholderResolver<R, Object, ? extends F>) weighted.value();

            @SuppressWarnings({"unchecked", "RedundantCast"}) // This should be equivalent.
            @Nullable final Map<String, Either<ConclusionValue<? extends F>, ContinuanceValue<?>>> resolverResult =
                (Map<String, Either<ConclusionValue<? extends F>, ContinuanceValue<?>>>) (Map<String, ?>) placeholderResolver
                    .resolve(continuancePlaceholderName, value, receiver, moonshineMethod.owner().getType(),
                        moonshineMethod.reflectMethod(), parameters);
            if (resolverResult == null) {
              // The resolver did not want to resolve this; pass it on.
              continue;
            }

            resolvingPlaceholderIterator.remove();

            resolverResult.forEach((resolvedName, resolvedValue) ->
                resolvedValue.map(conclusionValue -> finalisedPlaceholders.put(resolvedName, conclusionValue.value()),
                    continuanceValue -> resolvingPlaceholders.put(resolvedName, continuanceValue)));

            continue dancing;
          }

          throw new UnfinishedPlaceholderException(moonshineMethod, continuancePlaceholderName, value);
        }
      }
    }
  }
}

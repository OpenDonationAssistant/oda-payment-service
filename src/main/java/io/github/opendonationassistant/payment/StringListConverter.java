package io.github.opendonationassistant.payment;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.ConversionContext;
import io.micronaut.core.util.StringUtils;
import io.micronaut.data.model.runtime.convert.AttributeConverter;
import jakarta.inject.Singleton;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class StringListConverter
  implements AttributeConverter<List<String>, String> {

  private static final String SPLIT_CHAR = ";";

  @Override
  public @Nullable String convertToPersistedValue(
    @Nullable List<String> entityValue,
    @NonNull ConversionContext context
  ) {
    if (Objects.isNull(entityValue)) {
      return null;
    }
    return entityValue.stream().collect(Collectors.joining(SPLIT_CHAR));
  }

  @Override
  public @Nullable List<String> convertToEntityValue(
    @Nullable String persistedValue,
    @NonNull ConversionContext context
  ) {
    if (Objects.isNull(persistedValue)) {
      return List.of();
    }
    return Arrays
      .asList(persistedValue.split(SPLIT_CHAR))
      .stream()
      .filter(value -> StringUtils.isNotEmpty(value))
      .toList();
  }
}

package io.github.opendonationassistant.conversion;

import io.github.opendonationassistant.commons.Amount;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Singleton
public class ConversionService {

  @Inject
  public ConversionService() {}

  public Amount convert(Amount amount, String currency) {
    if (amount.getCurrency().equals(currency)) {
      return amount;
    }
    if ("UZS".equals(currency)) {
      var converted = BigDecimal.valueOf(amount.getMajor())
        .setScale(4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(163.47));
      return new Amount(
        0,
        converted.multiply(BigDecimal.valueOf(100)).intValue(),
        "UZS"
      );
    }
    return amount;
  }
}

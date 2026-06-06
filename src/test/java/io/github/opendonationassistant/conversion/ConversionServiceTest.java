package io.github.opendonationassistant.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.opendonationassistant.commons.Amount;
import org.junit.jupiter.api.Test;

public class ConversionServiceTest {

  @Test
  public void testConversion() {
    var service = new ConversionService();
    var converted = service.convert(new Amount(100, 0, "RUB"), "UZS");
    assertEquals(new Amount(0, 1634700, "UZS"), converted);
  }
}

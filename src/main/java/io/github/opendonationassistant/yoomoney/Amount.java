package io.github.opendonationassistant.yoomoney;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Amount {

  private double value;
  private String currency;

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}

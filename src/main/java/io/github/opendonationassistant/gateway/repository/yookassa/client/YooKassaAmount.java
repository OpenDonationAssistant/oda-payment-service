package io.github.opendonationassistant.gateway.repository.yookassa.client;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class YooKassaAmount {

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

package io.github.opendonationassistant.yoomoney;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class PaymentMethod {
  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}

package io.github.opendonationassistant.gateway.repository.yookassa.client;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class YooKassaPaymentMethod {
  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}

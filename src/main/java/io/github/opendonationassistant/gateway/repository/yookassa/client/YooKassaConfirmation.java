package io.github.opendonationassistant.gateway.repository.yookassa.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class YooKassaConfirmation {

  private String type;

  @JsonProperty("confirmation_token")
  private String confirmationToken;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getConfirmationToken() {
    return confirmationToken;
  }

  public void setConfirmationToken(String confirmationToken) {
    this.confirmationToken = confirmationToken;
  }
}

package io.github.opendonationassistant.gateway.repository.robokassa;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record RobokassaInvoice(
  @JsonProperty("invoiceID") String invoiceId,
  @JsonProperty("errorCode") String errorCode,
  @JsonProperty("errorMessage") String errorMessage
) {}

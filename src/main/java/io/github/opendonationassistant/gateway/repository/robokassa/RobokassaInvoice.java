package io.github.opendonationassistant.gateway.repository.robokassa;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class RobokassaInvoice {

  @JsonProperty("invoiceID")
  private String invoiceId;

  @JsonProperty("errorCode")
  private String errorCode;

  @JsonProperty("errorMessage")
  private String errorMessage;

  public String getInvoiceId() {
    return invoiceId;
  }

  public void setInvoiceId(String invoiceId) {
    this.invoiceId = invoiceId;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}

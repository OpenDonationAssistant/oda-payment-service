package io.github.opendonationassistant.yoomoney;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class YooMoneyPayment {

  private String id;
  private String status;
  private Amount amount;
  private Confirmation confirmation;
  private Boolean capture = true;

  @JsonProperty("payment_method")
  private PaymentMethod paymentMethod;

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  @JsonProperty("save_payment_method")
  private String savePaymentMethod = "false";

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Amount getAmount() {
    return amount;
  }

  public void setAmount(Amount amount) {
    this.amount = amount;
  }

  public Confirmation getConfirmation() {
    return confirmation;
  }

  public void setConfirmation(Confirmation confirmation) {
    this.confirmation = confirmation;
  }

  public Boolean getCapture() {
    return capture;
  }

  public void setCapture(Boolean capture) {
    this.capture = capture;
  }

  public String getSavePaymentMethod() {
    return savePaymentMethod;
  }

  public void setSavePaymentMethod(String savePaymentMethod) {
    this.savePaymentMethod = savePaymentMethod;
  }

  @Override
  public String toString() {
    return (
      "{\"_type\"=\"YooMoneyPayment\",\"id\"=\"" +
      id +
      "\", status\"=\"" +
      status +
      "\", amount\"=\"" +
      amount +
      "\", confirmation\"=\"" +
      confirmation +
      "\", capture\"=\"" +
      capture +
      "\", paymentMethod\"=\"" +
      paymentMethod +
      "\", savePaymentMethod\"=\"" +
      savePaymentMethod +
      "}"
    );
  }
}

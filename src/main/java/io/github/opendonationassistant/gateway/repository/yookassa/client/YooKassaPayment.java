package io.github.opendonationassistant.gateway.repository.yookassa.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class YooKassaPayment {

  private String id;
  private String status;
  private YooKassaAmount amount;
  private YooKassaConfirmation confirmation;
  private Boolean capture = true;

  @JsonProperty("payment_method")
  private YooKassaPaymentMethod paymentMethod;

  public YooKassaPaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(YooKassaPaymentMethod paymentMethod) {
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

  public YooKassaAmount getAmount() {
    return amount;
  }

  public void setAmount(YooKassaAmount amount) {
    this.amount = amount;
  }

  public YooKassaConfirmation getConfirmation() {
    return confirmation;
  }

  public void setConfirmation(YooKassaConfirmation confirmation) {
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
}

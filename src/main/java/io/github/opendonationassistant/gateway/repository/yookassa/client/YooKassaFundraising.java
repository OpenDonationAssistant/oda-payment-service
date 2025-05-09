package io.github.opendonationassistant.gateway.repository.yookassa.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class YooKassaFundraising {

  private String receiver;
  private String label;
  @JsonProperty("quickpay_form")
  private String quickpayForm = "button";
  private int sum;

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getQuickpayForm() {
    return quickpayForm;
  }

  public void setQuickpayForm(String quickpayForm) {
    this.quickpayForm = quickpayForm;
  }

  public int getSum() {
    return sum;
  }

  public void setSum(int sum) {
    this.sum = sum;
  }
}

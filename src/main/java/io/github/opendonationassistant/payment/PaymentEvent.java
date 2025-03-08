package io.github.opendonationassistant.payment;

import io.github.opendonationassistant.yoomoney.YooMoneyPayment;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class PaymentEvent {

  private String type;
  private String event;
  private YooMoneyPayment object;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public YooMoneyPayment getObject() {
    return object;
  }

  public void setObject(YooMoneyPayment object) {
    this.object = object;
  }

  @Override
  public String toString() {
    return """
      {
        "_type":"PaymentEvent",
        "type":"%s",
        "event":"%s",
        "object":"%s",
      }
    """.formatted(type, event, object);
  }
}

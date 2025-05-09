package io.github.opendonationassistant.payment.repository;

import io.github.opendonationassistant.events.PaymentNotificationSender;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import io.micronaut.serde.annotation.Serdeable;
import java.util.concurrent.CompletableFuture;

@Serdeable
public class Payment {

  private PaymentData data;
  protected PaymentDataRepository dataRepository;
  protected PaymentNotificationSender notificationSender;
  protected WordFilterRepository wordFilterRepository;

  public Payment(
    PaymentData data,
    PaymentDataRepository dataRepository,
    PaymentNotificationSender notificationSender,
    WordFilterRepository wordFilterRepository
  ) {
    this.data = data;
    this.dataRepository = dataRepository;
    this.notificationSender = notificationSender;
    this.wordFilterRepository = wordFilterRepository;
  }

  public void save() {
    if (dataRepository.getById(data.id()).isEmpty()) {
      dataRepository.save(data);
    } else {
      dataRepository.update(data);
    }
  }

  public CompletableFuture<Payment> complete(GatewayRepository gateways) {
    return CompletableFuture.supplyAsync(() -> this);
  }

  public PaymentData getData() {
    return this.data;
  }

  public static enum Status {
    COMPLETED("completed"),
    INITED("inited");

    private String value;

    private Status(String value) {
      this.value = value;
    }

    public String value() {
      return this.value;
    }

    public static Status from(String value) {
      return switch (value) {
        case "completed" -> COMPLETED;
        default -> INITED;
      };
    }
  }
}

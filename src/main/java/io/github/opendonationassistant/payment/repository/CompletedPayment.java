package io.github.opendonationassistant.payment.repository;

import io.github.opendonationassistant.events.PaymentNotificationSender;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class CompletedPayment extends Payment {

  public CompletedPayment(
    PaymentData data,
    PaymentDataRepository dataRepository,
    PaymentNotificationSender notificationSender,
    WordFilterRepository wordFilterRepository
  ) {
    super(data, dataRepository, notificationSender, wordFilterRepository);
  }
}

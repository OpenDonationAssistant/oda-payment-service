package io.github.opendonationassistant.payment.repository;

import io.github.opendonationassistant.events.payments.PaymentFacade;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class CompletedPayment extends Payment {

  public CompletedPayment(
    PaymentData data,
    PaymentDataRepository dataRepository,
    PaymentFacade facade,
    WordFilterRepository wordFilterRepository
  ) {
    super(data, dataRepository, facade, wordFilterRepository);
  }
}

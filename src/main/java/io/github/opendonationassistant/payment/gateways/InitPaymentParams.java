package io.github.opendonationassistant.payment.gateways;

import io.github.opendonationassistant.payment.amount.Amount;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record InitPaymentParams(String recipientId, String id, Amount amount) {}

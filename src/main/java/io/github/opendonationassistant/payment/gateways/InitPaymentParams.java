package io.github.opendonationassistant.payment.gateways;

import io.github.opendonationassistant.payment.amount.Amount;

public record InitPaymentParams(String recipientId, String id, Amount amount) {}

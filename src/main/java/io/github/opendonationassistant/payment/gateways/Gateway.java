package io.github.opendonationassistant.payment.gateways;

import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
import java.util.concurrent.CompletableFuture;

public interface Gateway {
  CompletableFuture<InitedPayment> init(InitPaymentParams params);
  CompletableFuture<String> status(String gatewayId);
}

package io.github.opendonationassistant.payment.gateways;

import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
import java.util.concurrent.CompletableFuture;

public interface Gateway {
  CompletableFuture<InitedPayment> init(InitPaymentParams params);
  CompletableFuture<String> status(String gatewayId);

  public static enum Type {
    YOOKASSA,
    YOOMONEY,
    ROBOKASSA,
    CRYPTOCLOUD;

    public static Type from(String type) {
      return switch (type) {
        case "robokassa" -> Type.ROBOKASSA;
        case "yoomoney" -> Type.YOOMONEY;
        default -> Type.YOOKASSA;
      };
    }
  }
}

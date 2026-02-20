package io.github.opendonationassistant.gateway;

import io.github.opendonationassistant.commons.Amount;
import io.micronaut.serde.annotation.Serdeable;
import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.Nullable;

public interface Gateway {
  CompletableFuture<InitResponse> init(InitPaymentParams params);
  CompletableFuture<String> status(String gatewayId);

  public static enum Type {
    YOOKASSA,
    YOOMONEY,
    ROBOKASSA,
    FAKE,
    CRYPTOCLOUD;

    public static Type from(String type) {
      return switch (type) {
        case "robokassa" -> Type.ROBOKASSA;
        case "yoomoney" -> Type.YOOMONEY;
        case "cryptocloud" -> Type.CRYPTOCLOUD;
        case "fake" -> Type.FAKE;
        default -> Type.YOOKASSA;
      };
    }
  }

  @Serdeable
  public static record InitPaymentParams(
    String recipientId,
    String id,
    Amount amount
  ) {}

  @Serdeable
  public static record InitResponse(
    String gateway,
    String gatewayId,
    @Nullable String operationUrl,
    String token
  ) {}
}

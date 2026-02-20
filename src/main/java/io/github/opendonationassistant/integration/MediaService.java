package io.github.opendonationassistant.integration;

import io.github.opendonationassistant.commons.Amount;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Client("media")
public interface MediaService {
  @Post("/commands/media/linkPayment")
  public CompletableFuture<LinkPaymentResponse> linkPayment(
    LinkPaymentCommand command
  );

  @Serdeable
  public static record LinkPaymentCommand(
    String recipientId,
    String paymentId,
    List<String> mediaIds
  ) {}

  @Serdeable
  public static record LinkPaymentResponse(Amount requiredAmount) {}
}

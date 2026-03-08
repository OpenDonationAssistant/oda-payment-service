package io.github.opendonationassistant.integration;

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;

import io.github.opendonationassistant.commons.Amount;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Client("media")
@Header(name = CONTENT_TYPE, value = "application/json")
public interface MediaService {

  @Post("/commands/media/linkPayment")
  public CompletableFuture<LinkPaymentResponse> linkPayment(
    @Body LinkPaymentCommand command
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

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

@Client("actions")
@Header(name = CONTENT_TYPE, value = "application/json")
public interface ActionsService {

  @Post("/actions/commands/link-actions")
  public CompletableFuture<LinkActionsResponse> linkPayment(
    @Body LinkActionsRequest request
  );

  @Serdeable
  record LinkActionsRequest(
    String source,
    String originId,
    List<ActionRequest> actions
  ) {}

  @Serdeable
  record ActionRequest(String actionId, Integer amount) {}

  @Serdeable
  record LinkActionsResponse(Amount amount) {}
}

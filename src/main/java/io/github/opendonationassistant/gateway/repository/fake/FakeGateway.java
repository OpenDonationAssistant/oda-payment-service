package io.github.opendonationassistant.gateway.repository.fake;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.gateway.Gateway;
import java.util.concurrent.CompletableFuture;

public class FakeGateway implements Gateway {

  @Override
  public CompletableFuture<InitResponse> init(InitPaymentParams params) {
    var id = Generators.timeBasedEpochGenerator().generate().toString();
    return CompletableFuture.completedFuture(
      new InitResponse("fake", id, "http://localhost:3000", "token")
    );
  }

  @Override
  public CompletableFuture<String> status(String gatewayId) {
    return CompletableFuture.completedFuture("completed");
  }
}

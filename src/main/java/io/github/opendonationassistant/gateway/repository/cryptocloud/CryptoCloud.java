package io.github.opendonationassistant.gateway.repository.cryptocloud;

import io.github.opendonationassistant.gateway.Gateway;
import io.github.opendonationassistant.gateway.repository.cryptocloud.CryptoCloudClient.CryptoCloudInvoiceRequest;
import io.github.opendonationassistant.gateway.repository.cryptocloud.CryptoCloudClient.CryptoCloudRequestResponse;
import java.util.concurrent.CompletableFuture;

public class CryptoCloud implements Gateway {

  private CryptoCloudClient client;
  private String shopId;
  private String apiKey;

  public CryptoCloud(CryptoCloudClient client, String shopId, String apiKey) {
    this.client = client;
    this.apiKey = apiKey;
    this.shopId = shopId;
  }

  @Override
  public CompletableFuture<InitResponse> init(InitPaymentParams params) {
    return client
      .init(
        "Token %s".formatted(this.apiKey),
        new CryptoCloudInvoiceRequest(this.shopId, params.amount().getMajor())
      )
      .thenApply(CryptoCloudRequestResponse::result)
      .thenApply(it -> new InitResponse("cryptocloud", it.uuid(), it.link(), ""));
  }

  @Override
  public CompletableFuture<String> status(String gatewayId) {
    return CompletableFuture.supplyAsync(() -> "completed");
  }
}

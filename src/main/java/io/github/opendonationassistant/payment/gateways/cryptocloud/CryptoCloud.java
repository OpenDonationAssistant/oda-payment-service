package io.github.opendonationassistant.payment.gateways.cryptocloud;

import io.github.opendonationassistant.payment.gateways.Gateway;
import io.github.opendonationassistant.payment.gateways.InitPaymentParams;
import io.github.opendonationassistant.payment.gateways.cryptocloud.CryptoCloudClient.CryptoCloudInvoiceRequest;
import io.github.opendonationassistant.payment.gateways.cryptocloud.CryptoCloudClient.CryptoCloudRequestResponse;
import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
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
  public CompletableFuture<InitedPayment> init(InitPaymentParams params) {
    return client
      .init(
        "Token %s".formatted(this.apiKey),
        new CryptoCloudInvoiceRequest(this.shopId, params.amount().getMajor())
      )
      .thenApply(CryptoCloudRequestResponse::result)
      .thenApply(it -> {
        var inited = new InitedPayment();
        inited.setGateway("cryptocloud");
        inited.setGatewayId(it.uuid());
        inited.setOperationUrl(it.link());
        return inited;
      });
  }

  @Override
  public CompletableFuture<String> status(String gatewayId) {
    return CompletableFuture.supplyAsync(() -> "completed");
  }
}

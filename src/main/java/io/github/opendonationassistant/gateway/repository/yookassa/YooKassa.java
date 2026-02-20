package io.github.opendonationassistant.gateway.repository.yookassa;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.gateway.Gateway;
import io.github.opendonationassistant.integration.YooKassaClient;
import io.github.opendonationassistant.integration.YooKassaClient.*;
import io.micronaut.http.BasicAuth;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class YooKassa implements Gateway {

  private final ODALogger log = new ODALogger(this);
  private final YooKassaClient client;
  private final String shopId;
  private final String shopToken;

  public YooKassa(String shopId, String shopToken, YooKassaClient client) {
    this.client = client;
    this.shopId = shopId;
    this.shopToken = shopToken;
  }

  public CompletableFuture<InitResponse> init(InitPaymentParams params) {
    log.info("Init YooKassa Payment", Map.of("params", params));

    return client
      .init(
        new BasicAuth(shopId, shopToken),
        asYooMoneyPequest(params.amount()),
        params.id()
      )
      .thenApply(created -> this.asInitedPayment(created, params.id()));
  }

  private InitResponse asInitedPayment(YooKassaPayment created, String id) {
    log.info("Received YooKassa Response", Map.of("response", created));

    var initedPayment = new InitResponse(
      "yookassa",
      created.id(),
      "/payment/%s".formatted(id),
      created.confirmation().confirmationToken()
    );

    return initedPayment;
  }

  private YooKassaPaymentRequest asYooMoneyPequest(Amount amount) {
    var yooMoneyAmount = new YooKassaAmount(amount.getMajor(), "RUB");
    var confirmation = new YooKassaConfirmationRequest("embedded");
    var payment = new YooKassaPaymentRequest(
      yooMoneyAmount,
      confirmation,
      false,
      "false"
    );

    log.info("Creating YooKassa Payment", Map.of("payment", payment));

    return payment;
  }

  @Override
  public CompletableFuture<String> status(String gatewayId) {
    return client
      .get(new BasicAuth(shopId, shopToken), gatewayId)
      .thenApply(payment -> {
        return !payment.status().equals("succeeded")
          ? "failed"
          : "completed";
      });
  }
}

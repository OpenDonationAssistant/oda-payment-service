package io.github.opendonationassistant.gateway.repository.yookassa;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.gateway.Gateway;
import io.github.opendonationassistant.gateway.repository.yookassa.client.YooKassaAmount;
import io.github.opendonationassistant.gateway.repository.yookassa.client.YooKassaClient;
import io.github.opendonationassistant.gateway.repository.yookassa.client.YooKassaConfirmation;
import io.github.opendonationassistant.gateway.repository.yookassa.client.YooKassaPayment;
import io.micronaut.http.BasicAuth;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class YooKassa implements Gateway {

  private final Logger log = LoggerFactory.getLogger(YooKassa.class);
  private final YooKassaClient client;
  private final String shopId;
  private final String shopToken;

  public YooKassa(String shopId, String shopToken, YooKassaClient client) {
    this.client = client;
    this.shopId = shopId;
    this.shopToken = shopToken;
  }

  public CompletableFuture<InitResponse> init(InitPaymentParams params) {
    MDC.put("context", ToString.asJson(Map.of("params", params)));
    log.info("Init YooKassa Payment");

    return client
      .init(
        new BasicAuth(shopId, shopToken),
        asYooMoneyPayment(params.amount()),
        params.id()
      )
      .thenApply(created -> this.asInitedPayment(created, params.id()));
  }

  private InitResponse asInitedPayment(YooKassaPayment created, String id) {
    MDC.put("context", ToString.asJson(Map.of("response", created)));
    log.info("Received YooKassa Response");

    var initedPayment = new InitResponse(
      "yookassa",
      created.getId(),
      "/payment/%s".formatted(id),
      created.getConfirmation().getConfirmationToken()
    );
    return initedPayment;
  }

  private YooKassaPayment asYooMoneyPayment(Amount amount) {
    YooKassaPayment payment = new YooKassaPayment();
    var yooMoneyAmount = new YooKassaAmount();
    yooMoneyAmount.setValue(amount.getMajor());
    yooMoneyAmount.setCurrency("RUB");
    payment.setAmount(yooMoneyAmount);
    var confirmation = new YooKassaConfirmation();
    confirmation.setType("embedded");
    payment.setConfirmation(confirmation);

    MDC.put("context", ToString.asJson(Map.of("payment", payment)));
    log.info("Creating YooKassa Payment");

    return payment;
  }

  @Override
  public CompletableFuture<String> status(String gatewayId) {
    return client
      .get(new BasicAuth(shopId, shopToken), gatewayId)
      .thenApply(payment -> {
        return !payment.getStatus().equals("succeeded")
          ? "failed"
          : "completed";
      });
  }
}

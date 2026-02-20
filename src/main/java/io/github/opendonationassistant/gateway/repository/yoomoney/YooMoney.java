package io.github.opendonationassistant.gateway.repository.yoomoney;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.gateway.Gateway;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class YooMoney implements Gateway {

  private ODALogger log = new ODALogger(this);

  public final HttpClient httpClient;
  public final Executor executor;
  public final String account;
  public final String recipientId;

  public YooMoney(
    String account,
    String recipientId,
    HttpClient httpClient,
    Executor executor
  ) {
    this.account = account;
    this.recipientId = recipientId;
    this.httpClient = httpClient;
    this.executor = executor;
  }

  public CompletableFuture<InitResponse> init(InitPaymentParams params) {
    var payment = new HashMap<String, Object>();
    payment.put("sum", params.amount().getMajor());
    payment.put("label", params.id());
    payment.put("receiver", account);
    payment.put("quickpay-form", "button");
    payment.put(
      "successURL",
      "https://%s.oda.digital/payment/%s/result".formatted(
          recipientId,
          params.id()
        )
    );

    HttpRequest<?> request = HttpRequest.POST(
      "/quickpay/confirm",
      payment
    ).contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE);

    return CompletableFuture.supplyAsync(
      () -> httpClient.toBlocking().exchange(request),
      executor
    ).thenApply(response -> {
      var operationUrl = response.getHeaders().get("location");

      log.info(
        "Received YooMoney Response",
        Map.of(
          "responseCode",
          response.getStatus().getCode(),
          "location",
          operationUrl
        )
      );

      return new InitResponse("yoomoney", "", operationUrl, "");
    });
  }

  @Override
  public CompletableFuture<String> status(String gatewayId) {
    return CompletableFuture.supplyAsync(() -> "completed");
  }
}

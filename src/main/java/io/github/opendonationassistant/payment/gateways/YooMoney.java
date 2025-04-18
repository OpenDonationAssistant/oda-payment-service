package io.github.opendonationassistant.payment.gateways;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
import io.github.opendonationassistant.yoomoney.Fundraising;
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

  private Logger log = LoggerFactory.getLogger(YooMoney.class);

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

  public CompletableFuture<InitedPayment> init(InitPaymentParams params) {
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

      MDC.put(
        "context",
        ToString.asJson(
          Map.of(
            "responseCode",
            response.getStatus().getCode(),
            "location",
            operationUrl
          )
        )
      );
      log.info("Received YooMoney Response");

      var inited = new InitedPayment();
      inited.setGateway("yoomoney");
      // todo inited.setGatewayId(response.getInvoiceId());
      inited.setOperationUrl(operationUrl);
      return inited;
    });
  }

  @Override
  public CompletableFuture<String> status(String gatewayId) {
    return CompletableFuture.supplyAsync(() -> "completed");
  }
}

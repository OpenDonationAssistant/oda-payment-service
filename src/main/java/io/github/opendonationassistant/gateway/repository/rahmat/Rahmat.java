package io.github.opendonationassistant.gateway.repository.rahmat;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.conversion.ConversionService;
import io.github.opendonationassistant.gateway.Gateway;
import io.github.opendonationassistant.gateway.repository.rahmat.RahmatClient.InvoiceRequest;
import io.github.opendonationassistant.gateway.repository.rahmat.RahmatClient.Ofd;
import io.github.opendonationassistant.gateway.repository.rahmat.RahmatClient.TokenRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Rahmat implements Gateway {

  private final ODALogger log = new ODALogger(this);

  private final RahmatClient client;
  private final ConversionService conversion;
  private final String appSecret;
  private final String appId;
  private final Long storeId;
  private final String packageCode;
  private final String mxik;

  public Rahmat(
    RahmatClient client,
    ConversionService conversion,
    String appId,
    String appSecret,
    String mxik,
    String packageCode,
    Long storeId
  ) {
    this.client = client;
    this.conversion = conversion;
    this.appId = appId;
    this.appSecret = appSecret;
    this.mxik = mxik;
    this.packageCode = packageCode;
    this.storeId = storeId;
  }

  @Override
  public CompletableFuture<InitResponse> init(InitPaymentParams params) {
    var amount = Long.valueOf(
      conversion.convert(params.amount(), "UZS").getMinor()
    );
    return client
      .getToken(new TokenRequest(appId, appSecret))
      .thenCompose(response -> {
        log.info("Received Rahmat Token", Map.of("response", response));
        return client.create(
          "Bearer %s".formatted(response.token()),
          new InvoiceRequest(
            storeId,
            amount,
            params.id(),
            "ru",
            "https://oda.digital/payment/%s/result".formatted(params.id()),
            "https://api.oda.digital/notification/rahmat",
            List.of(new Ofd(1L, amount, mxik, amount, packageCode, "Донат"))
          )
        );
      })
      .thenApply(invoice ->
        new InitResponse(
          "rahmat",
          invoice.data().uuid(),
          invoice.data().checkoutUrl(),
          ""
        )
      );
  }

  @Override
  public CompletableFuture<String> status(String gatewayId) {
    return CompletableFuture.completedFuture("completed");
  }
}

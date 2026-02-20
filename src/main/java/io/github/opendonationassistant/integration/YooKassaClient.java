package io.github.opendonationassistant.integration;

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.http.BasicAuth;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.serde.annotation.Serdeable;
import java.util.concurrent.CompletableFuture;

@Client(id = "yoomoney")
@Header(name = CONTENT_TYPE, value = "application/json")
public interface YooKassaClient {

  @Post("/v3/payments")
  CompletableFuture<YooKassaPayment> init(
    BasicAuth auth,
    @Body YooKassaPaymentRequest payment,
    @Header(name = "Idempotence-Key") String idempotenceKey
  );

  @Get("/v3/payments/{paymentId}")
  CompletableFuture<YooKassaPayment> get(
    BasicAuth auth,
    @PathVariable("paymentId") String paymentId
  );

  @Serdeable
  public record YooKassaPaymentRequest(
    YooKassaAmount amount,
    YooKassaConfirmationRequest confirmation,
    Boolean capture,
    @JsonProperty("save_payment_method") String savePaymentMethod
  ) {}

  @Serdeable
  public record YooKassaPayment(
    String id,
    String status,
    YooKassaAmount amount,
    YooKassaConfirmation confirmation,
    @JsonProperty("payment_method") YooKassaPaymentMethod paymentMethod
  ) {}

  @Serdeable
  public record YooKassaAmount(double value, String currency) {}

  @Serdeable
  public record YooKassaConfirmationRequest(String type) {}

  @Serdeable
  public record YooKassaConfirmation(
    @JsonProperty("confirmation_token") String confirmationToken
  ) {}

  @Serdeable
  public record YooKassaPaymentMethod(String type) {}
}

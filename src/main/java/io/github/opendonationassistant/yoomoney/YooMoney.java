package io.github.opendonationassistant.yoomoney;

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;

import java.util.concurrent.CompletableFuture;

import io.micronaut.http.BasicAuth;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(id = "yoomoney")
@Header(name = CONTENT_TYPE, value = "application/json")
public interface YooMoney {

  @Post("/v3/payments")
  CompletableFuture<YooMoneyPayment> init(
    BasicAuth auth,
    @Body YooMoneyPayment payment,
    @Header(name = "Idempotence-Key") String idempotenceKey
  );

  @Get("/v3/payments/{paymentId}")
  CompletableFuture<YooMoneyPayment> get(
    BasicAuth auth,
    @PathVariable("paymentId") String paymentId
  );
}

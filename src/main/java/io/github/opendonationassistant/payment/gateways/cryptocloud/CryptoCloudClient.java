package io.github.opendonationassistant.payment.gateways.cryptocloud;

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import java.util.concurrent.CompletableFuture;

@Client(id = "cryptocloud")
@Header(name = CONTENT_TYPE, value = "application/json")
public interface CryptoCloudClient {
  @Post(value = "/v2/invoice/create")
  CompletableFuture<CryptoCloudRequestResponse> init(
    @Header(name = "Authorization") String authorization,
    @Body CryptoCloudInvoiceRequest request
  );

  public static record CryptoCloudInvoiceRequest(
    String shop_id,
    double amount
  ) {}

  public static record CryptoCloudInvoice(String uuid, String link) {}

  public static record CryptoCloudRequestResponse(
    String status,
    CryptoCloudInvoice result
  ) {}
}

package io.github.opendonationassistant.gateway.repository.cryptocloud;

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.serde.annotation.Serdeable;
import java.util.concurrent.CompletableFuture;

@Client(id = "cryptocloud")
@Header(name = CONTENT_TYPE, value = "application/json")
public interface CryptoCloudClient {
  @Post(value = "/v2/invoice/create")
  CompletableFuture<CryptoCloudRequestResponse> init(
    @Header(name = "Authorization") String authorization,
    @Body CryptoCloudInvoiceRequest request
  );

  @Serdeable
  public static record CryptoCloudInvoiceRequest(
    String shop_id,
    double amount,
    String currency
  ) {}

  @Serdeable
  public static record CryptoCloudInvoice(String uuid, String link) {}

  @Serdeable
  public static record CryptoCloudRequestResponse(
    String status,
    CryptoCloudInvoice result
  ) {}
}

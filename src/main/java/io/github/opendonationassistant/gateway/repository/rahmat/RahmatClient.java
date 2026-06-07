package io.github.opendonationassistant.gateway.repository.rahmat;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Client("rahmat")
@Header(name = "Content-Type", value = "application/json")
public interface RahmatClient {
  @Post("/auth")
  public CompletableFuture<TokenResponse> getToken(@Body TokenRequest request);

  @Post("/payment/invoice")
  public CompletableFuture<ResponseWrapper<CreatedInvoice>> create(
    @Header(name = "Authorization") String token,
    @Body InvoiceRequest payment
  );

  @Serdeable
  public static record TokenRequest(
    @JsonProperty("application_id") String appId,
    @JsonProperty("secret") String secret
  ) {}

  @Serdeable
  public static record TokenResponse(String token) {}

  @Serdeable
  public static record ResponseWrapper<T>(boolean success, T data) {}

  @Serdeable
  public static record Ofd(
    Long qty,
    Long price,
    String mxik,
    Long total,
    @JsonProperty("package_code") String packageCode,
    String name
  ) {}

  @Serdeable
  public static record InvoiceRequest(
    @JsonProperty("store_id") Long storeId,
    Long amount,
    @JsonProperty("invoice_id") String invoiceId,
    String lang,
    String returnUrl,
    String callbackUrl,
    List<Ofd> ofd
  ) {}

  @Serdeable
  public static record CreatedInvoice(
    @JsonProperty("checkout_url") String checkoutUrl,
    String uuid
  ) {}
}

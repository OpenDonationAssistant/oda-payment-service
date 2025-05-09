package io.github.opendonationassistant.gateway.repository.robokassa;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(id = "robokassa")
public interface RobokassaClient {
  @Post(
    value = "/Merchant/Indexjson.aspx",
    produces = MediaType.APPLICATION_FORM_URLENCODED
  )
  CompletableFuture<RobokassaInvoice> init(@Body Map<String,String> payment);
}

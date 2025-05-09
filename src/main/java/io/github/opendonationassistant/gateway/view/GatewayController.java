package io.github.opendonationassistant.gateway.view;

import java.util.List;

import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsDataRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;

@Controller
public class GatewayController {
  private final GatewayCredentialsDataRepository credentialsRepository;

  @Inject
  public GatewayController(GatewayCredentialsDataRepository credentialsDataRepository){
    this.credentialsRepository = credentialsDataRepository;
  }

  @Get("/payments/gateways")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public HttpResponse<List<GatewayData>> listGateways(
    @QueryValue("recipientId") String recipientId
  ) {
    return HttpResponse.ok(
      credentialsRepository
        .findByRecipient(recipientId)
        .stream()
        .map(GatewayCredentialsData::asGatewayData)
        .toList()
    );
  }

  @Serdeable
  public static record GatewayData(String id, String gateway, String type, Boolean enabled) {}
}

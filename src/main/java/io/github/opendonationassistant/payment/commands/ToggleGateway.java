package io.github.opendonationassistant.payment.commands;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.recipient.GatewayCredentialsData;
import io.github.opendonationassistant.recipient.GatewayCredentialsDataRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Controller
public class ToggleGateway extends BaseController {

  private Logger log = LoggerFactory.getLogger(ToggleGateway.class);

  private GatewayCredentialsDataRepository credentialsDataRepository;

  @Inject
  public ToggleGateway(
    GatewayCredentialsDataRepository credentialsDataRepository
  ) {
    this.credentialsDataRepository = credentialsDataRepository;
  }

  @Post("/payments/commands/togglegateway")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> toggleGateway(
    Authentication auth,
    @Body ToggleGatewayCommand command
  ) {
    var recipientId = getOwnerId(auth);
    if (recipientId.isEmpty()) {
      return HttpResponse.unauthorized();
    }

    MDC.put(
      "context",
      ToString.asJson(
        Map.of("command", command, "recipientId", recipientId.get())
      )
    );
    log.info("Processing ToggleGatewayCommand");

    @NonNull
    final Optional<GatewayCredentialsData> gateway = credentialsDataRepository
      .findById(command.id())
      .filter(it -> it.getRecipient().equals(recipientId.get()));

    if (gateway.isEmpty()) {
      return HttpResponse.unauthorized();
    }

    gateway
      .map(GatewayCredentialsData::toggle)
      .ifPresent(credentialsDataRepository::update);
    return HttpResponse.ok();
  }

  @Serdeable
  public static record ToggleGatewayCommand(String id) {}
}

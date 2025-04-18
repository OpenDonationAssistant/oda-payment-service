package io.github.opendonationassistant.payment.commands;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.recipient.GatewayCredentialsData;
import io.github.opendonationassistant.recipient.GatewayCredentialsDataRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Controller
public class SetGateway extends BaseController {

  private Logger log = LoggerFactory.getLogger(SetGateway.class);

  private GatewayCredentialsDataRepository repository;

  @Inject
  public SetGateway(GatewayCredentialsDataRepository repository) {
    this.repository = repository;
  }

  @Post("/payments/commands/setgateway")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> setGateway(
    Authentication auth,
    @Body SetGatewayCommand command
  ) {
    var recipientId = getOwnerId(auth);
    if (recipientId.isEmpty()) {
      return HttpResponse.unauthorized();
    }

    MDC.put("command", ToString.asJson(command));
    MDC.put("recipientId", recipientId.get());
    log.info("Processing SetGatewayCommand");

    final GatewayCredentialsData data = new GatewayCredentialsData(
      command.id(),
      recipientId.get(),
      command.gatewayId(),
      command.token(),
      command.gateway(),
      command.secret(),
      command.enabled()
    );

    repository
      .findById(data.getId())
      .ifPresentOrElse(
        it -> repository.update(data),
        () -> repository.save(data)
      );
    return HttpResponse.ok();
  }

  @Serdeable
  public static record SetGatewayCommand(
    String id,
    String gatewayId,
    String token,
    String gateway,
    String secret,
    boolean enabled
  ) {}

}

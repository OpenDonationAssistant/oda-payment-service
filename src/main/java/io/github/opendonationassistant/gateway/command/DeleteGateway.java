package io.github.opendonationassistant.gateway.command;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsDataRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import java.util.Optional;

@Controller
public class DeleteGateway extends BaseController {

  private GatewayCredentialsDataRepository repository;

  @Inject
  public DeleteGateway(GatewayCredentialsDataRepository repository) {
    this.repository = repository;
  }

  @Post("/payments/commands/deletegateway")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> deleteGateway(
    Authentication auth,
    @Body DeleteGatewayCommand command
  ) {
    final Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    repository.deleteById(command.id());
    return HttpResponse.ok();
  }

  @Serdeable
  public static record DeleteGatewayCommand(String id) {}
}

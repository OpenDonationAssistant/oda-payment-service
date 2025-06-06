package io.github.opendonationassistant.gateway.command;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.events.config.ConfigCommandSender;
import io.github.opendonationassistant.events.config.ConfigPutCommand;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsDataRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
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
  private ConfigCommandSender configCommandSender;

  @Inject
  public ToggleGateway(
    GatewayCredentialsDataRepository credentialsDataRepository,
    ConfigCommandSender configCommandSender
  ) {
    this.credentialsDataRepository = credentialsDataRepository;
    this.configCommandSender =  configCommandSender;
  }

  @Post("/payments/commands/togglegateway")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  @ExecuteOn(TaskExecutors.BLOCKING)
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

    if (!gateway.get().isEnabled()) {
      credentialsDataRepository
        .findByRecipient(recipientId.get())
        .stream()
        .filter(it -> it.isEnabled())
        .forEach(it -> credentialsDataRepository.update(it.toggle()));

      var configCommand = new ConfigPutCommand();
      configCommand.setName("paymentpage");
      configCommand.setKey("gateway");
      configCommand.setValue(gateway.get().getGateway());
      configCommand.setOwnerId(recipientId.get());
      configCommandSender.send(configCommand);
    }

    gateway
      .map(GatewayCredentialsData::toggle)
      .ifPresent(credentialsDataRepository::update);

    return HttpResponse.ok();
  }

  @Serdeable
  public static record ToggleGatewayCommand(String id) {}
}

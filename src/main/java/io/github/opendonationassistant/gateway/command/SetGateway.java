package io.github.opendonationassistant.gateway.command;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.events.config.ConfigCommandSender;
import io.github.opendonationassistant.events.config.ConfigPutCommand;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
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
import jakarta.transaction.Transactional;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Controller
public class SetGateway extends BaseController {

  private Logger log = LoggerFactory.getLogger(SetGateway.class);

  private GatewayCredentialsDataRepository repository;
  private ConfigCommandSender configCommandSender;

  @Inject
  public SetGateway(GatewayCredentialsDataRepository repository, ConfigCommandSender configCommandSender) {
    this.repository = repository;
    this.configCommandSender = configCommandSender;
  }

  @Post("/payments/commands/setgateway")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Transactional
  public HttpResponse<Void> setGateway(
    Authentication auth,
    @Body SetGatewayCommand command
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
    log.info("Processing SetGatewayCommand");

    final GatewayCredentialsData data = new GatewayCredentialsData(
      command.id(),
      recipientId.get(),
      command.gatewayId(),
      command.token(),
      command.gateway(),
      command.secret(),
      command.type(),
      command.enabled()
    );

    if (command.enabled()) {
      repository
        .findByRecipient(recipientId.get())
        .stream()
        .filter(gateway -> gateway.isEnabled())
        .map(gateway -> gateway.toggle())
        .forEach(repository::update);
    }

    repository
      .findById(data.getId())
      .ifPresentOrElse(
        it -> repository.update(data),
        () -> repository.save(data)
      );

    var configCommand = new ConfigPutCommand();
    configCommand.setName("paymentpage");
    configCommand.setKey("gateway");
    configCommand.setValue(command.gateway());
    configCommand.setOwnerId(recipientId.get());
    configCommandSender.send(configCommand);

    return HttpResponse.ok();
  }

  @Serdeable
  public static record SetGatewayCommand(
    String id,
    String gatewayId,
    String token,
    String gateway,
    String secret,
    String type,
    boolean enabled
  ) {}
}

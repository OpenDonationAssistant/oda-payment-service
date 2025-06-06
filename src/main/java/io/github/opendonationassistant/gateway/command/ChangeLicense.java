package io.github.opendonationassistant.gateway.command;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.events.config.ConfigCommandSender;
import io.github.opendonationassistant.events.config.ConfigPutCommand;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Controller
public class ChangeLicense extends BaseController {

  private Logger log = LoggerFactory.getLogger(ChangeLicense.class);

  private ConfigCommandSender configCommandSender;

  @Inject
  public ChangeLicense(ConfigCommandSender configCommandSender) {
    this.configCommandSender = configCommandSender;
  }

  @Post("/payments/commands/changelicense")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  @ExecuteOn(TaskExecutors.BLOCKING)
  public HttpResponse<Void> changeLicense(
    Authentication auth,
    @Body ChangeLicenseCommand command
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
    log.info("Processing ChangeLicenseCommand");

    var configCommand = new ConfigPutCommand();
    configCommand.setName("paymentpage");
    configCommand.setKey("gateway");
    configCommand.setValue(command.gateway());
    configCommand.setOwnerId(recipientId.get());
    configCommandSender.send(configCommand);

    return HttpResponse.ok();
  }

  @Serdeable
  public static record ChangeLicenseCommand(String gateway) {}
}

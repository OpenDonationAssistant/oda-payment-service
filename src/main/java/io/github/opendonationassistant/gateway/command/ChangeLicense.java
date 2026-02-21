package io.github.opendonationassistant.gateway.command;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.events.config.ConfigCommand.PutKeyValue;
import io.github.opendonationassistant.events.config.ConfigCommandSender;
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

@Controller
public class ChangeLicense extends BaseController {

  private ODALogger log = new ODALogger(this);

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

    log.info(
      "Processing ChangeLicenseCommand",
      Map.of("command", command, "recipientId", recipientId.get())
    );

    configCommandSender.send(
      new PutKeyValue(
        recipientId.get(),
        "paymentpage",
        "gateway",
        command.gateway()
      )
    );

    return HttpResponse.ok();
  }

  @Serdeable
  public static record ChangeLicenseCommand(String gateway) {}
}

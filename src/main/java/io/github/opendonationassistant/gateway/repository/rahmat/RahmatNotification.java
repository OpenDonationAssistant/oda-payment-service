package io.github.opendonationassistant.gateway.repository.rahmat;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.gateway.AbstractGatewayRepository;
import io.github.opendonationassistant.payment.repository.PaymentRepository;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.inject.Inject;
import java.util.Map;

@Controller
public class RahmatNotification {

  private final ODALogger log = new ODALogger(this);

  private final PaymentRepository payments;
  private final AbstractGatewayRepository gateways;

  @Inject
  public RahmatNotification(
    PaymentRepository payments,
    AbstractGatewayRepository gateways
  ) {
    this.payments = payments;
    this.gateways = gateways;
  }

  @Post("/notification/rahmat")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Secured(SecurityRule.IS_ANONYMOUS)
  @ExecuteOn(TaskExecutors.BLOCKING)
  @Operation(hidden = true)
  public void handleRahmatEvent(@Body Notification notification) {
    log.info("Rahmat Payment Event", Map.of("notification", notification));

    payments
      .getById(notification.invoiceId())
      .ifPresent(payment -> payment.complete(gateways));
  }

  @Serdeable
  public static record Notification(
    @JsonProperty("invoice_id") String invoiceId
  ) {}
}

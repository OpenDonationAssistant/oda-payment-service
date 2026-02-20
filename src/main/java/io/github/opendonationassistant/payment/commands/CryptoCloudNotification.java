package io.github.opendonationassistant.payment.commands;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.payment.repository.PaymentRepository;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import java.util.Map;

@Controller
public class CryptoCloudNotification {

  private final ODALogger log = new ODALogger(this);

  private final PaymentRepository payments;
  private final GatewayRepository gateways;

  @Inject
  public CryptoCloudNotification(
    PaymentRepository payments,
    GatewayRepository gateways
  ) {
    this.payments = payments;
    this.gateways = gateways;
  }

  @Post("/notification/cryptocloud")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Secured(SecurityRule.IS_ANONYMOUS)
  @ExecuteOn(TaskExecutors.BLOCKING)
  public void handleCryptocloudEvent(String invoice_id, String status) {
    log.info(
      "CryptoCloud Payment Event",
      Map.of("invoice_id", invoice_id, "status", status)
    );

    if (!"success".equals(status)) {
      return;
    }

    payments
      .getByGatewayId("INV-%s".formatted(invoice_id))
      .ifPresent(payment -> payment.complete(gateways));
  }
}

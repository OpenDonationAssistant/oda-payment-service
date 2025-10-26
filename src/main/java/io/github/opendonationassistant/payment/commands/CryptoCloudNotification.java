package io.github.opendonationassistant.payment.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.gateway.GatewayRepository;
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
import jakarta.inject.Inject;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Controller
public class CryptoCloudNotification {

  private Logger log = LoggerFactory.getLogger(CryptoCloudNotification.class);

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
  public void handleCryptocloudEvent(@Body Map<String, Object> event) {
    MDC.put("context", ToString.asJson(Map.of("event", event)));
    log.info("CryptoCloud Payment Event");

    if (!"success".equals(event.get("status"))) {
      return;
    }

    payments
      .getByGatewayId("INV-%s".formatted(event.get("invoice_id")))
      .map(payment -> payment.complete(gateways));
  }

  @Serdeable
  public static record PaymentEvent(
    String status,
    @JsonProperty("invoice_id")
    String invoiceId,
    @JsonProperty("invoice_info")
    Invoice invoiceInfo
  ) {}

  @Serdeable
  public static record Invoice(String uuid) {}
}

package io.github.opendonationassistant.payment.commands;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.gateway.repository.yookassa.client.YooKassaPayment;
import io.github.opendonationassistant.payment.repository.PaymentRepository;
import io.micronaut.http.annotation.Body;
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
public class YooKassaNotification {

  private Logger log = LoggerFactory.getLogger(YooKassaNotification.class);

  private final PaymentRepository payments;
  private final GatewayRepository gateways;

  @Inject
  public YooKassaNotification(
    PaymentRepository payments,
    GatewayRepository gateways
  ) {
    this.payments = payments;
    this.gateways = gateways;
  }

  @Post("/notification/yookassa")
  @Secured(SecurityRule.IS_ANONYMOUS)
  @ExecuteOn(TaskExecutors.BLOCKING)
  public void handleYookassaEvent(@Body PaymentEvent event) {
    MDC.put("context", ToString.asJson(Map.of("event", event)));
    log.info("YooKassa Payment Event");

    if ("payment.canceled".equals(event.event())) {
      return;
    }

    payments
      .getByGatewayId(event.object().getId())
      .map(payment -> payment.complete(gateways));
  }

  @Serdeable
  public static record PaymentEvent(
    String type,
    String event,
    YooKassaPayment object
  ) {}
}

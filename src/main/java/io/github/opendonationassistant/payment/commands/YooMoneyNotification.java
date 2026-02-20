package io.github.opendonationassistant.payment.commands;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.payment.repository.PaymentRepository;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Controller
public class YooMoneyNotification {

  private Logger log = LoggerFactory.getLogger(YooMoneyNotification.class);
  private GatewayRepository gateways;
  private PaymentRepository payments;

  @Inject
  public YooMoneyNotification(
    GatewayRepository gateways,
    PaymentRepository payments
  ) {
    this.gateways = gateways;
    this.payments = payments;
  }

  @Post(
    value = "/notification/yoomoney",
    consumes = MediaType.APPLICATION_FORM_URLENCODED
  )
  @Secured(SecurityRule.IS_ANONYMOUS)
  @ExecuteOn(TaskExecutors.BLOCKING)
  public void handleYooMoneyEvent(@Body Map<String, Object> event) {
    MDC.put("context", ToString.asJson(Map.of("event", event)));
    log.info("YooMoney Payment Event");

    Optional.ofNullable(event.get("label"))
      .flatMap(paymentId -> {
        return payments.getById((String) paymentId);
      })
      .ifPresent(payment -> payment.complete(gateways));
  }
}

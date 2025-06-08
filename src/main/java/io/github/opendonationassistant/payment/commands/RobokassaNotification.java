package io.github.opendonationassistant.payment.commands;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.payment.repository.PaymentRepository;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Controller
public class RobokassaNotification {

  private Logger log = LoggerFactory.getLogger(RobokassaNotification.class);

  private PaymentRepository payments;
  private GatewayRepository gateways;

  @Inject
  public RobokassaNotification(
    PaymentRepository payments,
    GatewayRepository gateways
  ) {
    this.payments = payments;
    this.gateways = gateways;
  }

  @Get(value = "/notification/robokassa", produces = MediaType.TEXT_PLAIN)
  @Secured(SecurityRule.IS_ANONYMOUS)
  @ExecuteOn(TaskExecutors.BLOCKING)
  public String handleRobokassaEvent(
    @QueryValue("SignatureValue") String signature,
    @QueryValue("SHP_ID") String id,
    @QueryValue("InvId") String invoice
  ) {
    MDC.put(
      "context",
      ToString.asJson(
        Map.of("signatureValue", signature, "shopId", id, "invId", invoice)
      )
    );
    log.info("Robokassa Payment Event");
    MDC.clear();

    //try {
    //  Thread.sleep(30000); // TODO: handle simultanious commands
    //} catch (Exception e) {}

    payments.getById(id).map(payment -> payment.complete(gateways).join());
    return "OK%s".formatted(invoice);
  }
}

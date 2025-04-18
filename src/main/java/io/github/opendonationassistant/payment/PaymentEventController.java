package io.github.opendonationassistant.payment;

import io.github.opendonationassistant.payment.commands.completepayment.CompletePaymentCommand;
import io.github.opendonationassistant.payment.gateways.GatewayProvider;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PaymentEventController {

  private Logger log = LoggerFactory.getLogger(PaymentEventController.class);

  private final PaymentRepository payments;
  private final GatewayProvider gatewayProvider;

  @Inject
  public PaymentEventController(
    PaymentRepository payments,
    GatewayProvider gatewayProvider
  ) {
    this.payments = payments;
    this.gatewayProvider = gatewayProvider;
  }

  private void handleYookassa(PaymentEvent event) {
    log.info("PaymentEvent: {}", event);
    if ("payment.canceled".equals(event.getEvent())) {
      return;
    }
    try {
      Thread.sleep(10000); // todo handle simultanious commands
    } catch (Exception e) {}
    payments
      .getByGatewayId(event.getObject().getId())
      .ifPresent(payment ->
        new CompletePaymentCommand(payment.getId()).execute(gatewayProvider)
      );
  }

  @Post("/notification/tabularussia")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public void deprecatedEndpoint(@Body PaymentEvent event) {
    handleYookassa(event);
  }

  @Post("/notification/yookassa")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public void handleYookassaEvent(@Body PaymentEvent event) {
    handleYookassa(event);
  }

  @Post(
    value = "/notification/yoomoney",
    consumes = MediaType.APPLICATION_FORM_URLENCODED
  )
  @Secured(SecurityRule.IS_ANONYMOUS)
  public void handleYooMoneyEvent(@Body Map<String, Object> event) {
    log.info("YooMoneyEvent: {}", event);
    Optional.ofNullable(event.get("label")).ifPresent(paymentId -> {
      new CompletePaymentCommand((String) paymentId).execute(gatewayProvider);
    });
  }

  @Get(value = "/notification/robokassa", produces = MediaType.TEXT_PLAIN)
  @Secured(SecurityRule.IS_ANONYMOUS)
  public String handleRobokassaEvent(
    @QueryValue("SignatureValue") String signature,
    @QueryValue("SHP_ID") String id,
    @QueryValue("InvId") String invoice
  ) {
    log.info("SignatureValue: {}, SHP_ID:  {}, InvId", signature, id, invoice);
    try {
      Thread.sleep(10000); // todo handle simultanious commands
    } catch (Exception e) {}
    payments
      .findById(id)
      .ifPresent(payment ->
        new CompletePaymentCommand(payment.getId()).execute(gatewayProvider)
      );
    return "OK%s".formatted(invoice);
  }
}

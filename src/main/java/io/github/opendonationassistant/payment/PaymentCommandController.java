package io.github.opendonationassistant.payment;

import io.github.opendonationassistant.payment.commands.completepayment.CompletePaymentCommand;
import io.github.opendonationassistant.payment.commands.createpayment.CreatePaymentCommand;
import io.github.opendonationassistant.payment.completedpayment.CompletedPayment;
import io.github.opendonationassistant.payment.gateways.GatewayProvider;
import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PaymentCommandController {

  private final Logger log = LoggerFactory.getLogger(
    PaymentCommandController.class
  );

  private final GatewayProvider gatewayProvider;

  public PaymentCommandController(GatewayProvider gatewayProvider) {
    this.gatewayProvider = gatewayProvider;
  }

  @Put("/payments/commands/create")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public CompletableFuture<InitedPayment> createDraft(
    @Body CreatePaymentCommand command
  ) {
    return command.execute(gatewayProvider);
  }

  @Put("/payments/commands/complete")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public CompletableFuture<CompletedPayment> complete(
    @Body CompletePaymentCommand command
  ) {
    log.info("{}", command);
    return command.execute(gatewayProvider);
  }
}

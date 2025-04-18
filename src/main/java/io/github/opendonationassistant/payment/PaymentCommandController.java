package io.github.opendonationassistant.payment;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.payment.commands.completepayment.CompletePaymentCommand;
import io.github.opendonationassistant.payment.commands.createpayment.CreatePaymentCommand;
import io.github.opendonationassistant.payment.completedpayment.CompletedPayment;
import io.github.opendonationassistant.payment.gateways.GatewayProvider;
import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
import io.github.opendonationassistant.recipient.GatewayCredentialsDataRepository;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Controller
public class PaymentCommandController {

  private final Logger log = LoggerFactory.getLogger(
    PaymentCommandController.class
  );

  private final GatewayProvider gatewayProvider;
  private final GatewayCredentialsDataRepository credentialsRepository;

  public PaymentCommandController(
    GatewayProvider gatewayProvider,
    GatewayCredentialsDataRepository credentialsRepository
  ) {
    this.gatewayProvider = gatewayProvider;
    this.credentialsRepository = credentialsRepository;
  }

  @Put("/payments/commands/create")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public CompletableFuture<InitedPayment> createDraft(
    @Body CreatePaymentCommand command
  ) {
    MDC.put("context", ToString.asJson(Map.of("command", command)));
    log.info("Processing CreatePaymentCommand");

    return command.execute(gatewayProvider);
  }

  @Put("/payments/commands/complete")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public CompletableFuture<CompletedPayment> complete(
    @Body CompletePaymentCommand command
  ) {
    MDC.put("context", ToString.asJson(Map.of("command", command)));
    log.info("Processing CompletePaymentCommand");

    return command.execute(gatewayProvider);
  }
}

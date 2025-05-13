package io.github.opendonationassistant.payment.commands;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.payment.repository.PaymentRepository;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.zalando.problem.Problem;

@Controller
public class CompletePayment {

  private Logger log = LoggerFactory.getLogger(CompletePayment.class);

  private PaymentRepository payments;
  private GatewayRepository gateways;

  public CompletePayment(
    PaymentRepository payments,
    GatewayRepository gateways
  ) {
    this.gateways = gateways;
    this.payments = payments;
  }

  @Put("/payments/commands/complete")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public CompletableFuture<CompletePaymentResponse> complete(
    @Body CompletePaymentCommand command
  ) {
    MDC.put("context", ToString.asJson(Map.of("command", command)));
    log.info("Processing CompletePaymentCommand");

    return payments
      .getById(command.paymentId())
      .orElseThrow(() -> Problem.builder().withTitle("Missing payment").build())
      .complete(gateways)
      .thenApply(payment -> new CompletePaymentResponse(payment.getData().id(), payment.getData().status()));
  }

  @Serdeable
  public static record CompletePaymentCommand(String id, String paymentId) {}

  @Serdeable
  public static record CompletePaymentResponse(String id, String status) {}
}

package io.github.opendonationassistant.payment;

import io.github.opendonationassistant.payment.commands.completepayment.CompletePaymentCommand;
import io.github.opendonationassistant.payment.commands.createpayment.CreatePaymentCommand;
import io.github.opendonationassistant.payment.commands.setgateway.SetGatewayCommand;
import io.github.opendonationassistant.payment.completedpayment.CompletedPayment;
import io.github.opendonationassistant.payment.gateways.GatewayProvider;
import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
import io.github.opendonationassistant.recipient.GatewayCredentialsDataRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  @Put("/payments/commands/setgateway")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> setGateway(
    Authentication auth,
    @Body SetGatewayCommand command
  ) {
    final Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    command.execute(ownerId.get(), credentialsRepository);
    return HttpResponse.ok();
  }

  private Optional<String> getOwnerId(Authentication auth) {
    return Optional.ofNullable(
      auth.getAttributes().get("preferred_username")
    ).map(String::valueOf);
  }
}

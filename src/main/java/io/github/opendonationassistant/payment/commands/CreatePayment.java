package io.github.opendonationassistant.payment.commands;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.gateway.Gateway.InitPaymentParams;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.payment.repository.Payment;
import io.github.opendonationassistant.payment.repository.PaymentData;
import io.github.opendonationassistant.payment.repository.PaymentRepository;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Controller
public class CreatePayment {

  private final Logger log = LoggerFactory.getLogger(CreatePayment.class);

  private final GatewayRepository gateways;
  private final PaymentRepository payments;

  @Inject
  public CreatePayment(GatewayRepository gateways, PaymentRepository payments) {
    this.gateways = gateways;
    this.payments = payments;
  }

  @Put("/payments/commands/create")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public CompletableFuture<CreatePaymentResponse> createDraft(
    @Body CreatePaymentCommand command
  ) {
    MDC.put("context", ToString.asJson(Map.of("command", command)));
    log.info("Processing CreatePaymentCommand");

    return gateways
      .get(command.recipientId(), command.gatewayCredId())
      .init(
        new InitPaymentParams(
          command.recipientId(),
          command.id(),
          command.amount()
        )
      )
      .thenApply(result -> {
        final List<PaymentData.Action> actions = Optional.ofNullable(
          command.actions()
        )
          .orElseGet(() -> List.of())
          .stream()
          .map(it ->
            new PaymentData.Action(
              it.id(),
              it.actionId(),
              it.amount(),
              it.parameters()
            )
          )
          .toList();
        final Optional<PaymentData.Auction> auction = Optional.ofNullable(
          command.vote()
        ).map(it -> new PaymentData.Auction(it.id(), it.item(), it.isNew()));
        final Payment payment = payments.from(
          new PaymentData(
            command.id(),
            result.gateway(),
            result.gatewayId(),
            Optional.ofNullable(command.method()).orElse(""),
            command.nickname(),
            command.message(),
            command.recipientId(),
            command.amount(),
            result.token(),
            command.gatewayCredId(),
            command.goal(),
            null,
            Instant.now(),
            Payment.Status.INITED.value(),
            command.attachments(),
            actions,
            auction.get()
          )
        );
        payment.save();
        if ("fake".equals(result.gateway())) {
          payment.complete(gateways);
        }
        return new CreatePaymentResponse(result.operationUrl(), result.token());
      });
  }

  @Serdeable
  public static record CreatePaymentCommand(
    String id,
    String gatewayCredId,
    String nickname,
    String message,
    String recipientId,
    String method,
    Amount amount,
    List<String> attachments,
    @Nullable String goal,
    List<Action> actions,
    @Nullable Vote vote,
    @Nullable String marker
  ) {
    @Serdeable
    public static record Vote(
      @Nullable String id,
      @Nullable String item,
      Boolean isNew
    ) {}
    @Serdeable
    public static record Action(
      String id,
      String actionId,
      Integer amount,
      Map<String, Object> parameters
    ) {}
  }

  @Serdeable
  public static record CreatePaymentResponse(
    @Nullable String operationUrl,
    String token
  ) {}
}

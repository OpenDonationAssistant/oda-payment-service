package io.github.opendonationassistant.payment.commands;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.events.PaymentNotificationSender;
import io.github.opendonationassistant.gateway.Gateway.InitPaymentParams;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.payment.repository.Payment;
import io.github.opendonationassistant.payment.repository.PaymentData;
import io.github.opendonationassistant.payment.repository.PaymentData.Auction;
import io.github.opendonationassistant.payment.repository.PaymentDataRepository;
import io.github.opendonationassistant.payment.repository.PaymentRepository;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

@Controller
public class CreatePayment {

  private final Logger log = LoggerFactory.getLogger(CreatePayment.class);
  private List<String> banned = List.of("0196f2a4-b6f7-7e47-9a01-3e3cfad8da1a");

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

    if (command.marker() != null && banned.contains(command.marker())) {
      throw Problem.builder().build();
    }

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
          .map(it -> new PaymentData.Action(it.name(), it.properties()))
          .toList();
        final PaymentData.Auction auction = new PaymentData.Auction(
          Optional.ofNullable(command.auction())
            .map(it -> it.item())
            .orElseGet(() -> ""),
          Optional.ofNullable(command.auction())
            .map(it -> it.isNew())
            .orElseGet(() -> true)
        );
        payments
          .from(
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
              Payment.Status.INITED.value(),
              command.attachments(),
              actions,
              auction
            )
          )
          .save();
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
    String goal,
    List<Action> actions,
    Auction auction,
    @Nullable String marker
  ) {
    @Serdeable
    public static record Auction(String item, Boolean isNew) {}
    @Serdeable
    public static record Action(String name, Map<String, Object> properties) {}
  }

  @Serdeable
  public static record CreatePaymentResponse(
    String operationUrl,
    String token
  ) {}
}

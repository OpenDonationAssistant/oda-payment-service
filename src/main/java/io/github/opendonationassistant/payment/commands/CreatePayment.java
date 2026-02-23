package io.github.opendonationassistant.payment.commands;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.gateway.Gateway.InitPaymentParams;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.integration.MediaService;
import io.github.opendonationassistant.integration.MediaService.LinkPaymentCommand;
import io.github.opendonationassistant.integration.MediaService.LinkPaymentResponse;
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

@Controller
public class CreatePayment {

  private final ODALogger log = new ODALogger(this);

  private final GatewayRepository gateways;
  private final PaymentRepository payments;
  private final MediaService mediaService;

  @Inject
  public CreatePayment(
    GatewayRepository gateways,
    PaymentRepository payments,
    MediaService mediaService
  ) {
    this.gateways = gateways;
    this.payments = payments;
    this.mediaService = mediaService;
  }

  @Put("/payments/commands/create")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public CompletableFuture<CreatePaymentResponse> createDraft(
    @Body CreatePaymentCommand command
  ) {
    log.info("Processing CreatePaymentCommand", Map.of("command", command));

    final CompletableFuture<Amount> requiredAmount = command
        .attachments()
        .isEmpty()
      ? CompletableFuture.completedFuture(new Amount(0, 0, "RUB"))
      : mediaService
        .linkPayment(
          new LinkPaymentCommand(
            command.recipientId(),
            command.id(),
            command.attachments()
          )
        )
        .thenApply(LinkPaymentResponse::requiredAmount);

    return requiredAmount
      .thenCompose(amount ->
        gateways
          .get(command.recipientId(), command.gatewayCredId())
          .init(
            new InitPaymentParams(
              command.recipientId(),
              command.id(),
              command.amount()
            )
          )
      )
      .thenCompose(result -> {
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
            auction.orElse(null)
          )
        );
        payment.save();
        if ("fake".equals(result.gateway())) {
          return payment
            .complete(gateways)
            .thenApply(it ->
              new CreatePaymentResponse(result.operationUrl(), result.token())
            );
        }
        return CompletableFuture.completedFuture(
          new CreatePaymentResponse(result.operationUrl(), result.token())
        );
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

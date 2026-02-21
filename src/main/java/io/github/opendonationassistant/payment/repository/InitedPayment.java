package io.github.opendonationassistant.payment.repository;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.github.opendonationassistant.events.payments.PaymentFacade;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.integration.MediaService;
import io.github.opendonationassistant.integration.MediaService.LinkPaymentCommand;
import io.github.opendonationassistant.wordblacklist.WordFilter;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import io.micronaut.serde.annotation.Serdeable;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Serdeable
public class InitedPayment extends Payment {

  private final ODALogger log = new ODALogger(this);
  private final MediaService mediaService;

  public InitedPayment(
    PaymentData data,
    PaymentDataRepository dataRepository,
    PaymentFacade facade,
    WordFilterRepository wordFilterRepository,
    MediaService mediaService
  ) {
    super(data, dataRepository, facade, wordFilterRepository);
    this.mediaService = mediaService;
  }

  public CompletableFuture<Payment> complete(GatewayRepository gateways) {
    log.info("Authorizing payment", Map.of("payment", this));
    final PaymentData payment = this.getData();
    return mediaService
      .linkPayment(
        new LinkPaymentCommand(
          payment.recipientId(),
          payment.id(),
          payment.attachments()
        )
      )
      .thenCompose(response ->
        gateways
          .get(payment.recipientId(), payment.gatewayCredentialId())
          .status(payment.gatewayId())
      )
      .thenCompose(status -> {
        // TODO: check status
        var result = "completed".equals(status) ? "completed" : "failed";
        log.debug(
          "payment: {}, status: {}",
          Map.of("id", payment.id(), "result", result)
        );
        var timestamp = Instant.now();
        var updatedData = payment
          .withAuthorizationTimestamp(timestamp)
          .withStatus(result);
        log.info("Payment completed", Map.of("payment", updatedData));
        // TODO filter alert notification
        final WordFilter wordFilter = wordFilterRepository.getByRecipientId(
          payment.recipientId()
        );
        log.info(
          "Sending notification",
          Map.of(
            "paymentId",
            updatedData.id(),
            "recipientId",
            updatedData.recipientId()
          )
        );
        // TODO split into 2 stages
        return facade
          .sendEvent(
            new PaymentEvent(
              updatedData.id(),
              updatedData.nickname(),
              wordFilter.filter(updatedData.nickname()),
              updatedData.message(),
              updatedData.nickname(),
              updatedData.recipientId(),
              payment.amount(),
              payment.attachments(),
              payment.goal(),
              timestamp,
              payment
                .actions()
                .stream()
                .map(action ->
                  new PaymentEvent.ActionRequest(
                    action.id(),
                    action.actionId(),
                    action.amount(),
                    action.parameters()
                  )
                )
                .toList(),
              Optional.ofNullable(updatedData.auction())
                .map(auction ->
                  new PaymentEvent.Vote(
                    auction.id(),
                    auction.item(),
                    auction.isNew()
                  )
                )
                .orElse(null)
            )
          )
          .thenApply(ignored -> {
            final CompletedPayment completed = new CompletedPayment(
              updatedData,
              this.dataRepository,
              this.facade,
              this.wordFilterRepository
            );
            completed.save();
            return completed;
          });
      });
  }
}

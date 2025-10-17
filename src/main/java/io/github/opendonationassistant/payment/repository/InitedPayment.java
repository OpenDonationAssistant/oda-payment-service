package io.github.opendonationassistant.payment.repository;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.events.PaymentNotificationSender;
import io.github.opendonationassistant.events.payments.PaymentFacade;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.wordblacklist.WordFilter;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import io.micronaut.serde.annotation.Serdeable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Serdeable
public class InitedPayment extends Payment {

  private final ODALogger log = new ODALogger(this);

  public InitedPayment(
    PaymentData data,
    PaymentDataRepository dataRepository,
    PaymentNotificationSender notificationSender,
    WordFilterRepository wordFilterRepository
  ) {
    super(data, dataRepository, notificationSender, wordFilterRepository);
  }

  public CompletableFuture<Payment> complete(GatewayRepository gateways) {
    log.info("Authorizing payment", Map.of("payment", this));
    return gateways
      .get(this.getData().recipientId(), this.getData().gatewayCredentialId())
      .status(this.getData().gatewayId())
      .thenApply(status -> {
        // TODO: check status
        var result = "completed".equals(status) ? "completed" : "failed";
        log.debug(
          "payment: {}, status: {}",
          Map.of("id", this.getData().id(), "result", result)
        );
        var updatedData =
          this.getData()
            .withAuthorizationTimestamp(Instant.now())
            .withStatus(result);
        log.info("Payment completed", Map.of("context", updatedData));
        // TODO filter alert notification
        final WordFilter wordFilter = wordFilterRepository.getByRecipientId(
          this.getData().recipientId()
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
        notificationSender.send(
          new CompletedPaymentNotification(
            updatedData.id(),
            updatedData.nickname(),
            wordFilter.filter(updatedData.nickname()),
            updatedData.message(),
            wordFilter.filter(updatedData.message()),
            updatedData.recipientId(),
            updatedData.amount(),
            updatedData.attachments(),
            updatedData.goal(),
            updatedData.authorizationTimestamp(),
            "ODA",
            Optional.ofNullable(this.getData().actions())
              .orElse(List.of())
              .stream()
              .map(action ->
                new PaymentFacade.ActionRequest(
                  action.id(),
                  action.actionId(),
                  action.parameters()
                )
              )
              .toList()
          )
        );
        final CompletedPayment completed = new CompletedPayment(
          updatedData,
          this.dataRepository,
          this.notificationSender,
          this.wordFilterRepository
        );
        completed.save();
        return completed;
      });
  }

  @Override
  public String toString() {
    return ToString.asJson(this);
  }
}

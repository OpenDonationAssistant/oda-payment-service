package io.github.opendonationassistant.payment.repository;

import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.events.PaymentNotificationSender;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.wordblacklist.WordFilter;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import io.micronaut.serde.annotation.Serdeable;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Serdeable
public class InitedPayment extends Payment {

  private final Logger log = LoggerFactory.getLogger(InitedPayment.class);

  public InitedPayment(
    PaymentData data,
    PaymentDataRepository dataRepository,
    PaymentNotificationSender notificationSender,
    WordFilterRepository wordFilterRepository
  ) {
    super(data, dataRepository, notificationSender, wordFilterRepository);
  }

  public CompletableFuture<Payment> complete(GatewayRepository gateways) {
    log.debug("authorizing payment: {}", this);
    return gateways
      .get(this.getData().recipientId(), this.getData().gatewayCredentialId())
      .status(this.getData().gatewayId())
      .thenApply(status -> {
        // TODO: check status
        var result = "completed".equals(status) ? "completed" : "failed";
        log.debug("payment: {}, status: {}", this.getData().id(), result);
        var updatedData =
          this.getData()
            .withAuthorizationTimestamp(Instant.now())
            .withStatus(result);
        final WordFilter wordFilter = wordFilterRepository.getByRecipientId(
          this.getData().recipientId()
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
            updatedData.authorizationTimestamp()
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

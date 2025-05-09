package io.github.opendonationassistant.payment.repository;

import io.github.opendonationassistant.events.PaymentNotificationSender;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class PaymentRepository {

  private PaymentDataRepository dataRepository;
  private PaymentNotificationSender notificationSender;
  private WordFilterRepository wordFilterRepository;

  @Inject
  public PaymentRepository(
    PaymentDataRepository dataRepository,
    PaymentNotificationSender notificationSender,
    WordFilterRepository wordFilterRepository
  ) {
    this.dataRepository = dataRepository;
    this.notificationSender = notificationSender;
    this.wordFilterRepository = wordFilterRepository;
  }

  public Optional<Payment> getById(String id) {
    return dataRepository.getById(id).map(this::from);
  }

  public Optional<Payment> getByGatewayId(String gatewayId) {
    return dataRepository.getByGatewayId(gatewayId).map(this::from);
  }

  public List<Payment> listByRecipientId(
    String recipientId,
    List<String> statuses
  ) {
    return dataRepository
      .getByRecipientIdAndStatusInOrderByAuthorizationTimestampDesc(
        recipientId,
        statuses
      )
      .stream()
      .map(this::from)
      .toList();
  }

  public Payment from(PaymentData data) {
    if ("completed".equals(data.status())) {
      return new CompletedPayment(
        data,
        dataRepository,
        notificationSender,
        wordFilterRepository
      );
    } else {
      return new InitedPayment(
        data,
        dataRepository,
        notificationSender,
        wordFilterRepository
      );
    }
  }
}

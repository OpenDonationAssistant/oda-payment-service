package io.github.opendonationassistant.payment.repository;

import io.github.opendonationassistant.events.payments.PaymentFacade;
import io.github.opendonationassistant.integration.MediaService;
import io.github.opendonationassistant.wordblacklist.WordFilterRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class PaymentRepository {

  private final PaymentDataRepository dataRepository;
  private final PaymentFacade facade;
  private final WordFilterRepository wordFilterRepository;
  private final MediaService mediaService;

  @Inject
  public PaymentRepository(
    PaymentDataRepository dataRepository,
    PaymentFacade facade,
    WordFilterRepository wordFilterRepository,
    MediaService mediaService
  ) {
    this.dataRepository = dataRepository;
    this.facade = facade;
    this.wordFilterRepository = wordFilterRepository;
    this.mediaService = mediaService;
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
        facade,
        wordFilterRepository
      );
    } else {
      return new InitedPayment(
        data,
        dataRepository,
        facade,
        wordFilterRepository,
        mediaService
      );
    }
  }
}

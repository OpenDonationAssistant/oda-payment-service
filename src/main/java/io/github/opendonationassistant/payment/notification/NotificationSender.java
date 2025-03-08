package io.github.opendonationassistant.payment.notification;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;

@RabbitClient("amq.topic")
public interface NotificationSender {
  void send(@Binding String binding, CompletedPaymentNotification notification);
}

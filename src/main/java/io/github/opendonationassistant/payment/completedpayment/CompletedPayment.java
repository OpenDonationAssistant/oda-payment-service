package io.github.opendonationassistant.payment.completedpayment;

import io.github.opendonationassistant.Beans;
import io.github.opendonationassistant.payment.Payment;
import io.github.opendonationassistant.payment.PaymentRepository;
import io.github.opendonationassistant.payment.notification.CompletedPaymentNotification;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class CompletedPayment extends Payment {

  public CompletedPayment(String status){
    setStatus(status);
  }

  public static CompletedPayment from(Payment origin){
    var payment = new CompletedPayment(origin.getStatus());
    payment.setId(origin.getId());
    payment.setGatewayId(origin.getGatewayId());
    payment.setMethod(origin.getMethod());
    payment.setNickname(origin.getNickname());
    payment.setMessage(origin.getMessage());
    payment.setAmount(origin.getAmount());
    payment.setRecipientId(origin.getRecipientId());
    payment.setConfirmation(origin.getConfirmation());
    payment.setAttachments(origin.getAttachments());
    payment.setAuthorizationTimestamp(origin.getAuthorizationTimestamp());
    payment.setGoal(origin.getGoal());
    return payment;
  }

  public void save() {
    PaymentRepository repository = Beans.get(
      PaymentRepository.class
    );
    repository.update(this);
  }

  public CompletedPaymentNotification createNotification(){
    var notification = new CompletedPaymentNotification();
    notification.setId(getId());
    notification.setNickname(getNickname());
    notification.setMessage(getMessage());
    notification.setRecipientId(getRecipientId());
    notification.setAmount(getAmount());
    notification.setConfirmation(getConfirmation());
    notification.setFailed("failed".equals(getStatus()));
    notification.setAttachments(getAttachments());
    notification.setAuthorizationTimestamp(getAuthorizationTimestamp());
    notification.setGoal(getGoal());
    return notification;
  }

}

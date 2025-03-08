package io.github.opendonationassistant.payment.notification;

import io.github.opendonationassistant.Beans;
import io.github.opendonationassistant.payment.amount.Amount;
import io.micronaut.serde.annotation.Serdeable;
import java.time.Instant;

@Serdeable
public class CompletedPaymentNotification {

  private String id;
  private String nickname;
  private String senderName;
  private String message;
  private String recipientId;
  private Amount amount;
  private String confirmation;
  private Boolean failed;
  private java.util.List<String> attachments;
  private String goal;
  private Instant authorizationTimestamp;

  public void send() {
    NotificationSender sender = Beans.get(NotificationSender.class);
    sender.send("payments", this);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    nickname = WordFilter.filter(nickname);
    this.nickname = nickname;
    this.senderName = nickname;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    message = WordFilter.filter(message);
    this.message = message;
  }

  public String getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }

  public Amount getAmount() {
    return amount;
  }

  public void setAmount(Amount amount) {
    this.amount = amount;
  }

  public String getConfirmation() {
    return confirmation;
  }

  public void setConfirmation(String confirmation) {
    this.confirmation = confirmation;
  }

  public Boolean getFailed() {
    return failed;
  }

  public void setFailed(Boolean failed) {
    this.failed = failed;
  }

  public java.util.List<String> getAttachments() {
    return attachments;
  }

  public void setAttachments(java.util.List<String> attachments) {
    this.attachments = attachments;
  }

  public Instant getAuthorizationTimestamp() {
    return authorizationTimestamp;
  }

  public void setAuthorizationTimestamp(Instant authorizationTimestamp) {
    this.authorizationTimestamp = authorizationTimestamp;
  }

  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public String getGoal() {
    return goal;
  }

  public void setGoal(String goal) {
    this.goal = goal;
  }
}

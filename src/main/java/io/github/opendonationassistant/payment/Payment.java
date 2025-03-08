package io.github.opendonationassistant.payment;

import io.github.opendonationassistant.Beans;
import io.github.opendonationassistant.payment.amount.Amount;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;
import java.time.Instant;

@Serdeable
@MappedEntity("payment")
public class Payment {

  @Id
  private String id;

  private String gateway;
  private String gatewayId;
  private String method;
  private String nickname;
  private String message;
  private String recipientId;
  private Amount amount;
  private String confirmation;

  @MappedProperty(converter = StringListConverter.class)
  private java.util.List<String> attachments;

  private String goal;
  private Instant authorizationTimestamp;
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getGatewayId() {
    return gatewayId;
  }

  public void setGatewayId(String gatewayId) {
    this.gatewayId = gatewayId;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getGoal() {
    return goal;
  }

  public void setGoal(String goal) {
    this.goal = goal;
  }

  public String getGateway() {
    return gateway;
  }

  public void setGateway(String gateway) {
    this.gateway = gateway;
  }
}

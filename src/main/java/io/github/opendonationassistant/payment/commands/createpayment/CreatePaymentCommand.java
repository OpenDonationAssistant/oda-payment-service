package io.github.opendonationassistant.payment.commands.createpayment;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.payment.amount.Amount;
import io.github.opendonationassistant.payment.gateways.GatewayProvider;
import io.github.opendonationassistant.payment.gateways.InitPaymentParams;
import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Serdeable
public class CreatePaymentCommand {

  private final Logger log = LoggerFactory.getLogger(
    CreatePaymentCommand.class
  );

  private String id;
  private String nickname;
  private String message;
  private String recipientId;
  private String method;
  private Amount amount;
  private java.util.List<String> attachments;
  private String goal;

  public CreatePaymentCommand(
    String nickname,
    String message,
    Amount amount,
    String recipientId,
    List<String> attachments
  ) {
    this(null, nickname, message, amount, recipientId, attachments);
  }

  public CreatePaymentCommand(
    String id,
    String nickname,
    String message,
    Amount amount,
    String recipientId,
    List<String> attachments
  ) {
    this(id, nickname, message, amount, recipientId, attachments, null);
  }

  public CreatePaymentCommand(
    String id,
    String nickname,
    String message,
    Amount amount,
    String recipientId,
    List<String> attachments,
    String method
  ) {
    this.id =
      id == null
        ? Generators.timeBasedEpochGenerator().generate().toString()
        : id;
    this.nickname = nickname;
    this.message = message;
    this.amount = amount;
    this.recipientId = recipientId;
    this.attachments = attachments;
    this.method = method;
  }

  public CompletableFuture<InitedPayment> execute(
    GatewayProvider gatewayProvider
  ) {
    return gatewayProvider
      .get(recipientId)
      .init(new InitPaymentParams(recipientId, id, amount))
      .thenApply(result -> {
        result.setId(id);
        result.setMessage(message);
        result.setNickname(nickname);
        result.setAttachments(attachments);
        result.setAmount(amount);
        result.setRecipientId(recipientId);
        result.setMethod(method);
        result.setGoal(goal);
        result.save();
        return result;
      });
  }

  public String getNickname() {
    return nickname;
  }

  public String getMessage() {
    return message;
  }

  public Amount getAmount() {
    return amount;
  }

  public String getRecipientId() {
    return recipientId;
  }

  public java.util.List<String> getAttachments() {
    return attachments;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }

  public void setAmount(Amount amount) {
    this.amount = amount;
  }

  public void setAttachments(java.util.List<String> attachments) {
    this.attachments = attachments;
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
}

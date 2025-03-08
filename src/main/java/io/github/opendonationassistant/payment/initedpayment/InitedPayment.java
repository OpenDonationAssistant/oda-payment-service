package io.github.opendonationassistant.payment.initedpayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.opendonationassistant.Beans;
import io.github.opendonationassistant.payment.Payment;
import io.github.opendonationassistant.payment.PaymentRepository;
import io.github.opendonationassistant.payment.completedpayment.CompletedPayment;
import io.github.opendonationassistant.payment.gateways.GatewayProvider;
import io.micronaut.serde.annotation.Serdeable;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Serdeable
public class InitedPayment extends Payment {

  private final Logger log = LoggerFactory.getLogger(InitedPayment.class);

  private String operationUrl;

  public InitedPayment() {
    setStatus("inited");
  }

  public static InitedPayment from(Payment origin) {
    var payment = new InitedPayment();
    payment.setMethod(origin.getMethod());
    payment.setId(origin.getId());
    payment.setGatewayId(origin.getGatewayId());
    payment.setNickname(origin.getNickname());
    payment.setMessage(origin.getMessage());
    payment.setRecipientId(origin.getRecipientId());
    payment.setAmount(origin.getAmount());
    payment.setConfirmation(origin.getConfirmation());
    payment.setAttachments(origin.getAttachments());
    payment.setAuthorizationTimestamp(origin.getAuthorizationTimestamp());
    payment.setGoal(origin.getGoal());
    return payment;
  }

  public CompletableFuture<CompletedPayment> complete(
    GatewayProvider gatewayProvider
  ) {
    log.debug("authorizing payment: {}", this);
    return gatewayProvider
      .get(getRecipientId())
      .status(getGatewayId())
      .thenApply(status -> {
        var completedPayment = new CompletedPayment(status);
        completedPayment.setId(getId());
        completedPayment.setAmount(getAmount());
        completedPayment.setMessage(getMessage());
        completedPayment.setNickname(getNickname());
        completedPayment.setGatewayId(getGatewayId());
        completedPayment.setRecipientId(getRecipientId());
        completedPayment.setConfirmation(getConfirmation());
        completedPayment.setAttachments(getAttachments());
        completedPayment.setAuthorizationTimestamp(Instant.now());
        completedPayment.setGoal(getGoal());
        return completedPayment;
      });
  }

  public void save() {
    PaymentRepository repository = Beans.get(PaymentRepository.class);
    repository.save(this);
  }

  @Override
  public String toString() {
    var mapper = new ObjectMapper();
    var value = "can't be serialized as json";
    try {
      value = mapper.writeValueAsString(this);
    } catch (Exception e) {}
    return value;
  }

  public String getOperationUrl() {
    return operationUrl;
  }

  public void setOperationUrl(String operationUrl) {
    this.operationUrl = operationUrl;
  }
}

package io.github.opendonationassistant.payment.commands.completepayment;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.Beans;
import io.github.opendonationassistant.payment.Payment;
import io.github.opendonationassistant.payment.PaymentRepository;
import io.github.opendonationassistant.payment.completedpayment.CompletedPayment;
import io.github.opendonationassistant.payment.gateways.GatewayProvider;
import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
import io.github.opendonationassistant.payment.notification.CompletedPaymentNotification;
import io.micronaut.serde.annotation.Serdeable;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Serdeable
public class CompletePaymentCommand {

  private Logger log = LoggerFactory.getLogger(CompletePaymentCommand.class);

  private String id;
  private String paymentId;

  public CompletePaymentCommand(String paymentId) {
    this(null, paymentId);
  }

  public CompletePaymentCommand(String id, String paymentId) {
    this.id =
      id == null
        ? Generators.timeBasedEpochGenerator().generate().toString()
        : id;
    this.paymentId = paymentId;
  }

  public CompletableFuture<CompletedPayment> execute(
    GatewayProvider gatewayProvider
  ) {
    PaymentRepository repository = Beans.get(PaymentRepository.class);
    Payment savedPayment = repository
      .findById(paymentId)
      .orElseThrow(() -> new RuntimeException("missing payment"));
    if (
      "completed".equals(savedPayment.getStatus()) ||
      "failed".equals(savedPayment.getStatus())
    ) {
      return CompletableFuture
        .completedStage(CompletedPayment.from(savedPayment))
        .toCompletableFuture();
    }
    InitedPayment payment = InitedPayment.from(savedPayment);
    return payment
      .complete(gatewayProvider)
      .thenApply(completedPayment -> {
        log.info(
          "{} final status is {}, recipientId is {}, amount is {}",
          paymentId,
          completedPayment.getStatus(),
          completedPayment.getRecipientId(),
          completedPayment.getAmount().getMajor()
        );
        completedPayment.save();
        if ("completed".equals(completedPayment.getStatus())) {
          CompletedPaymentNotification notification =
            completedPayment.createNotification();
          notification.send();
        }
        return completedPayment;
      });
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }
}

package io.github.opendonationassistant.payment.gateways;

import io.github.opendonationassistant.payment.amount.Amount;
import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
import io.github.opendonationassistant.yoomoney.Confirmation;
import io.github.opendonationassistant.yoomoney.YooMoney;
import io.github.opendonationassistant.yoomoney.YooMoneyPayment;
import io.micronaut.http.BasicAuth;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YooKassa implements Gateway {

  private final Logger log = LoggerFactory.getLogger(YooKassa.class);
  private final YooMoney yooMoney;
  private final String shopId;
  private final String shopToken;

  public YooKassa(String shopId, String shopToken, YooMoney yooMoney) {
    this.yooMoney = yooMoney;
    this.shopId = shopId;
    this.shopToken = shopToken;
  }

  public CompletableFuture<InitedPayment> init(InitPaymentParams params) {
    log.info("Init payment: {} with keys: {}:{}", params, shopId, shopToken);
    return yooMoney
      .init(
        new BasicAuth(shopId, shopToken),
        asYooMoneyPayment(params.amount()),
        params.id()
      )
      .thenApply(this::asInitedPayment)
      .thenApply(inited -> {
        inited.setOperationUrl("/payment/%s".formatted(params.id()));
        return inited;
      });
  }

  private InitedPayment asInitedPayment(YooMoneyPayment created) {
    log.info("Received yookassa response: {}", created);
    var initedPayment = new InitedPayment();
    initedPayment.setGateway("yookassa");
    initedPayment.setGatewayId(created.getId());
    initedPayment.setConfirmation(
      created.getConfirmation().getConfirmationToken()
    );
    return initedPayment;
  }

  private YooMoneyPayment asYooMoneyPayment(Amount amount) {
    YooMoneyPayment payment = new YooMoneyPayment();
    var yooMoneyAmount = new io.github.opendonationassistant.yoomoney.Amount();
    yooMoneyAmount.setValue(amount.getMajor());
    yooMoneyAmount.setCurrency("RUB");
    payment.setAmount(yooMoneyAmount);
    var confirmation = new Confirmation();
    confirmation.setType("embedded");
    payment.setConfirmation(confirmation);
    // PaymentMethod paymentMethod = new PaymentMethod();
    // paymentMethod.setType(method);
    // payment.setPaymentMethod(paymentMethod);
    log.info("Creating payment: {}", payment);
    return payment;
  }

  @Override
  public CompletableFuture<String> status(String gatewayId) {
    return yooMoney
      .get(new BasicAuth(shopId, shopToken), gatewayId)
      .thenApply(payment -> {
        return !payment.getStatus().equals("succeeded")
          ? "failed"
          : "completed";
      });
  }
}

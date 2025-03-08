package io.github.opendonationassistant.payment.gateways;

import io.github.opendonationassistant.payment.initedpayment.InitedPayment;
import io.github.opendonationassistant.robokassa.RobokassaClient;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Robokassa implements Gateway {

  private final Logger log = LoggerFactory.getLogger(Robokassa.class);

  private final RobokassaClient client;
  private final String login;
  private final String password;

  public Robokassa(RobokassaClient client, String login, String password) {
    this.client = client;
    this.login = login;
    this.password = password;
  }

  @Override
  public CompletableFuture<InitedPayment> init(InitPaymentParams params) {
    var payment = new HashMap<String, String>();
    payment.put("MerchantLogin", login);
    payment.put("SHP_ID", params.id());
    payment.put("Description", "Donation to streamer");
    payment.put("OutSum", String.valueOf(params.amount().getMajor()));
    payment.put(
      "SignatureValue",
      getHash(
        "%s:%s::%s:SHP_ID=%s".formatted(
            login,
            String.valueOf(params.amount().getMajor()),
            password,
            params.id()
          )
      )
    );
    log.info("Payment: {}", payment);
    return client
      .init(payment)
      .thenApply(created -> {
        log.info("Created  by robokassa: {}", created);
        var inited = new InitedPayment();
        inited.setGateway("robokassa");
        inited.setGatewayId(created.getInvoiceId());
        inited.setOperationUrl(
          "https://auth.robokassa.ru/Merchant/Index/%s".formatted(
              created.getInvoiceId()
            )
        );
        return inited;
      });
  }

  private String getHash(String text) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      return String.format(
        "%032X",
        new BigInteger(1, md.digest(text.getBytes()))
      );
    } catch (NoSuchAlgorithmException e) {
      return "";
    }
  }

  @Override
  public CompletableFuture<String> status(String gatewayId) {
    return  CompletableFuture.supplyAsync(() -> "completed");
  }
}

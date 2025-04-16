package io.github.opendonationassistant.payment.gateways;

import io.github.opendonationassistant.recipient.GatewayCredentialsDataRepository;
import io.github.opendonationassistant.robokassa.RobokassaClient;
import io.github.opendonationassistant.yoomoney.YooMoney;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Singleton
public class GatewayProvider {

  private final GatewayCredentialsDataRepository credentialsProvider;
  private final YooMoney yooMoney;
  private final RobokassaClient robokassaClient;
  private final HttpClient yoomoneyHttpClient;
  private final Executor executor;

  public GatewayProvider(
    GatewayCredentialsDataRepository credProvider,
    YooMoney yooMoney,
    RobokassaClient robokassaClient,
    @Client("fundraising") HttpClient httpClient
  ) {
    this.credentialsProvider = credProvider;
    this.yooMoney = yooMoney;
    this.robokassaClient = robokassaClient;
    this.yoomoneyHttpClient = httpClient;
    this.executor = Executors.newFixedThreadPool(4);
  }

  public Gateway get(String recipientId) {
    var credentials = credentialsProvider.findByRecipient(recipientId);
    if (credentials.size() < 1) {
      throw new RuntimeException("No credentials for recipient " + recipientId);
    }
    var cred = credentials.get(0);
    String shopId = cred.getGatewayId();
    String shopToken = cred.getToken();
    switch (Gateway.Type.from(cred.getGateway())) {
      case ROBOKASSA:
        return new Robokassa(robokassaClient, shopId, shopToken);
      case CRYPTOCLOUD:
        return new Robokassa(robokassaClient, shopId, shopToken);
      case YOOMONEY:
        return new io.github.opendonationassistant.payment.gateways.YooMoney(
          shopId,
          recipientId,
          yoomoneyHttpClient,
          executor
        );
      default:
        return new YooKassa(shopId, shopToken, yooMoney);
    }
  }
}

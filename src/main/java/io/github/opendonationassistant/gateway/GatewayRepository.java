package io.github.opendonationassistant.gateway;

import io.github.opendonationassistant.gateway.repository.GatewayCredentialsDataRepository;
import io.github.opendonationassistant.gateway.repository.cryptocloud.CryptoCloud;
import io.github.opendonationassistant.gateway.repository.cryptocloud.CryptoCloudClient;
import io.github.opendonationassistant.gateway.repository.fake.FakeGateway;
import io.github.opendonationassistant.gateway.repository.robokassa.Robokassa;
import io.github.opendonationassistant.gateway.repository.robokassa.RobokassaClient;
import io.github.opendonationassistant.gateway.repository.yookassa.YooKassa;
import io.github.opendonationassistant.gateway.repository.yookassa.client.YooKassaClient;
import io.github.opendonationassistant.gateway.repository.yoomoney.YooMoney;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Singleton
public class GatewayRepository {

  private final GatewayCredentialsDataRepository credentialsProvider;
  private final YooKassaClient yooKassaClient;
  private final RobokassaClient robokassaClient;
  private final HttpClient yoomoneyHttpClient;
  private final Executor executor;
  private final CryptoCloudClient cryptoCloudClient;

  public GatewayRepository(
    GatewayCredentialsDataRepository credProvider,
    YooKassaClient yooKassaClient,
    RobokassaClient robokassaClient,
    CryptoCloudClient cryptoCloudClient,
    @Client("fundraising") HttpClient httpClient
  ) {
    this.credentialsProvider = credProvider;
    this.yooKassaClient = yooKassaClient;
    this.robokassaClient = robokassaClient;
    this.yoomoneyHttpClient = httpClient;
    this.cryptoCloudClient = cryptoCloudClient;
    this.executor = Executors.newFixedThreadPool(4);
  }

  public Gateway get(String recipientId, String credId) {
    var credentials = credentialsProvider
      .findByRecipient(recipientId)
      .stream()
      .filter(cred -> cred.getId().equals(credId))
      .filter(cred -> cred.isEnabled())
      .findFirst();
    if (credentials.isEmpty()) {
      throw new RuntimeException(
        "No credentials for recipient " + recipientId + " and credId " + credId
      );
    }
    var cred = credentials.get();
    String shopId = cred.getGatewayId();
    String shopToken = cred.getToken();
    switch (Gateway.Type.from(cred.getGateway())) {
      case ROBOKASSA:
        return new Robokassa(robokassaClient, shopId, shopToken);
      case CRYPTOCLOUD:
        return new CryptoCloud(cryptoCloudClient, shopId, shopToken);
      case YOOMONEY:
        return new YooMoney(shopId, recipientId, yoomoneyHttpClient, executor);
      case FAKE:
        return new FakeGateway();
      default:
        return new YooKassa(shopId, shopToken, yooKassaClient);
    }
  }
}

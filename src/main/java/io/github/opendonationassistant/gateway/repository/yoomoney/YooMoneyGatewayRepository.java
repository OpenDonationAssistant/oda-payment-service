package io.github.opendonationassistant.gateway.repository.yoomoney;

import io.github.opendonationassistant.gateway.Gateway;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Named("yoomoney")
@Singleton
public class YooMoneyGatewayRepository implements GatewayRepository {

  private final HttpClient yoomoneyHttpClient;
  private final Executor executor;

  public YooMoneyGatewayRepository(@Client("fundraising") HttpClient httpClient) {
    this.yoomoneyHttpClient = httpClient;
    this.executor = Executors.newFixedThreadPool(4);
  }

  @Override
  public Gateway convert(GatewayCredentialsData data) {
    return new YooMoney(
      data.getGatewayId(),
      data.getRecipient(),
      yoomoneyHttpClient,
      executor
    );
  }
}

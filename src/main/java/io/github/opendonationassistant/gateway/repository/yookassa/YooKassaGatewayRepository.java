package io.github.opendonationassistant.gateway.repository.yookassa;

import io.github.opendonationassistant.gateway.Gateway;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import io.github.opendonationassistant.integration.YooKassaClient;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named("yookassa")
@Singleton
public class YooKassaGatewayRepository implements GatewayRepository {

  private final YooKassaClient yooKassaClient;

  public YooKassaGatewayRepository(YooKassaClient yooKassaClient) {
    this.yooKassaClient = yooKassaClient;
  }

  @Override
  public Gateway convert(GatewayCredentialsData data) {
    return new YooKassa(data.getGatewayId(), data.getToken(), yooKassaClient);
  }
}

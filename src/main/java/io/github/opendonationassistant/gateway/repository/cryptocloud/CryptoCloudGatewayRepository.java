package io.github.opendonationassistant.gateway.repository.cryptocloud;

import io.github.opendonationassistant.gateway.Gateway;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named("cryptocloud")
@Singleton
public class CryptoCloudGatewayRepository implements GatewayRepository {

  private final CryptoCloudClient cryptoCloudClient;

  public CryptoCloudGatewayRepository(CryptoCloudClient cryptoCloudClient) {
    this.cryptoCloudClient = cryptoCloudClient;
  }

  @Override
  public Gateway convert(GatewayCredentialsData data) {
    return new CryptoCloud(cryptoCloudClient, data.getGatewayId(), data.getToken());
  }
}

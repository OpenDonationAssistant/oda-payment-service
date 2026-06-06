package io.github.opendonationassistant.gateway.repository.robokassa;

import io.github.opendonationassistant.gateway.Gateway;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named("robokassa")
@Singleton
public class RobokassaGatewayRepository implements GatewayRepository {

  private final RobokassaClient robokassaClient;

  public RobokassaGatewayRepository(RobokassaClient robokassaClient) {
    this.robokassaClient = robokassaClient;
  }

  @Override
  public Gateway convert(GatewayCredentialsData data) {
    return new Robokassa(robokassaClient, data.getGatewayId(), data.getToken());
  }
}

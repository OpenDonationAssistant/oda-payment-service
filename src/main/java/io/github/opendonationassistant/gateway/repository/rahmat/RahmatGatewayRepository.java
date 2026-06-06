package io.github.opendonationassistant.gateway.repository.rahmat;

import io.github.opendonationassistant.conversion.ConversionService;
import io.github.opendonationassistant.gateway.Gateway;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named("rahmat")
@Singleton
public class RahmatGatewayRepository implements GatewayRepository {

  private final RahmatClient rahmatClient;
  private final ConversionService conversion;

  public RahmatGatewayRepository(
    RahmatClient rahmatClient,
    ConversionService conversion
  ) {
    this.rahmatClient = rahmatClient;
    this.conversion = conversion;
  }

  @Override
  public Gateway convert(GatewayCredentialsData data) {
    return new Rahmat(
      rahmatClient,
      conversion,
      data.getSettings().getOrDefault("appId", ""),
      data.getSettings().getOrDefault("appSecret", ""),
      data.getSettings().getOrDefault("mxik", ""),
      data.getSettings().getOrDefault("packageCode", ""),
      Long.parseLong(data.getGatewayId())
    );
  }
}

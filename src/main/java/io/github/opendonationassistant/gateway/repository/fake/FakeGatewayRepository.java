package io.github.opendonationassistant.gateway.repository.fake;

import io.github.opendonationassistant.gateway.Gateway;
import io.github.opendonationassistant.gateway.GatewayRepository;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named("fake")
@Singleton
public class FakeGatewayRepository implements GatewayRepository {

  @Override
  public Gateway convert(GatewayCredentialsData data) {
    return new FakeGateway();
  }
}

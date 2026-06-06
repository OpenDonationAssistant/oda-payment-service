package io.github.opendonationassistant.gateway;

import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;

public interface GatewayRepository {
  Gateway convert(GatewayCredentialsData data);
}

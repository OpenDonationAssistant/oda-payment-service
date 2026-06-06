package io.github.opendonationassistant.gateway;

import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsDataRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class AbstractGatewayRepository {

  private final GatewayCredentialsDataRepository credentialsProvider;
  private final Map<String, GatewayRepository> implementations;

  @Inject
  public AbstractGatewayRepository(
    GatewayCredentialsDataRepository credProvider,
    Map<String, GatewayRepository> implementations
  ) {
    this.credentialsProvider = credProvider;
    this.implementations = implementations;
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
    return convert(cred);
  }

  public Gateway convert(GatewayCredentialsData data) {
    var implementation = implementations.get(data.getGateway());
    if (implementation == null) {
      implementation = implementations.get("yookassa");
    }
    if (implementation == null) {
      throw new RuntimeException(
        "No GatewayRepository implementation found for gateway: " +
        data.getGateway()
      );
    }
    return implementation.convert(data);
  }
}

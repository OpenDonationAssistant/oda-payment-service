package io.github.opendonationassistant.recipient;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("credentials")
public class GatewayCredentialsData {
  @Id
  private String id;
  private String recipient;
  private String gatewayId;
  private String token;
  private String gateway;

  public GatewayCredentialsData(String id, String recipient, String gatewayId, String token, String gateway) {
    this.id = id;
    this.recipient = recipient;
    this.gatewayId = gatewayId;
    this.token = token;
    this.gateway = gateway;
  }

  public String getId() {
    return id;
  }

  public String getRecipient() {
    return recipient;
  }

  public String getGatewayId() {
    return gatewayId;
  }

  public String getToken() {
    return token;
  }

  public String getGateway() {
    return gateway;
  }
}

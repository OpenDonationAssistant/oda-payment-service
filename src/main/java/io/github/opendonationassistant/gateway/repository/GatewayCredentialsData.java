package io.github.opendonationassistant.gateway.repository;

import io.github.opendonationassistant.gateway.view.GatewayController.GatewayData;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

@Serdeable
@MappedEntity("credentials")
public class GatewayCredentialsData {

  @Id
  private String id;

  private String recipient;
  private String gatewayId;
  private String token;
  private String gateway;
  private String secret;

  @MappedProperty(value = "settings", type = DataType.JSON)
  private Map<String, String> settings;

  @MappedProperty("gateway_type")
  private String type;

  private boolean enabled;

  public GatewayCredentialsData(
    String id,
    String recipient,
    String gatewayId,
    String token,
    String gateway,
    String secret,
    Map<String, String> settings,
    String type,
    boolean enabled
  ) {
    this.id = id;
    this.recipient = recipient;
    this.gatewayId = gatewayId;
    this.token = token;
    this.gateway = gateway;
    this.secret = secret;
    this.enabled = enabled;
    this.settings = settings;
    this.type = type;
  }

  public GatewayCredentialsData toggle() {
    return new GatewayCredentialsData(
      id,
      recipient,
      gatewayId,
      token,
      gateway,
      secret,
      settings,
      type,
      !enabled
    );
  }

  public GatewayData asGatewayData() {
    return new GatewayData(id, gateway, type, enabled);
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

  public String getType() {
    return type;
  }

  public String getSecret() {
    return secret;
  }

  public Map<String, String> getSettings() {
    return settings;
  }

  public boolean isEnabled() {
    return this.enabled;
  }
}

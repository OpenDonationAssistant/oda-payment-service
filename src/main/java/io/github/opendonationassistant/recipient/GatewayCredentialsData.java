package io.github.opendonationassistant.recipient;

import io.github.opendonationassistant.payment.PaymentController.GatewayData;
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
  private String secret;
  private boolean enabled;

  public GatewayCredentialsData(
    String id,
    String recipient,
    String gatewayId,
    String token,
    String gateway,
    String secret,
    boolean enabled
  ) {
    this.id = id;
    this.recipient = recipient;
    this.gatewayId = gatewayId;
    this.token = token;
    this.gateway = gateway;
    this.secret = secret;
    this.enabled = enabled;
  }

  public GatewayData asGatewayData() {
    return new GatewayData(id, gateway, enabled);
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

  public String getSecret() {
    return secret;
  }

  public boolean isEnabled() {
    return this.enabled;
  }
}

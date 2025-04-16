package io.github.opendonationassistant.payment.commands.setgateway;

import io.github.opendonationassistant.recipient.GatewayCredentialsData;
import io.github.opendonationassistant.recipient.GatewayCredentialsDataRepository;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class SetGatewayCommand {

  private String id;

  private String gatewayId;
  private String token;
  private String gateway;
  private String secret;
  private boolean enabled;

  public SetGatewayCommand(
    String id,
    String gatewayId,
    String token,
    String gateway,
    String secret,
    boolean enabled
  ) {
    this.id = id;
    this.gatewayId = gatewayId;
    this.token = token;
    this.gateway = gateway;
    this.secret = secret;
    this.enabled = enabled;
  }

  public void execute(
    String recipientId,
    GatewayCredentialsDataRepository repository
  ) {
    final GatewayCredentialsData data = new GatewayCredentialsData(
      id,
      recipientId,
      gatewayId,
      token,
      gateway,
      secret,
      enabled
    );
    repository
      .findById(data.getId())
      .ifPresentOrElse(
        it -> repository.update(data),
        () -> repository.save(data)
      );
  }
}

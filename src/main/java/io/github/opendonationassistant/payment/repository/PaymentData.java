package io.github.opendonationassistant.payment.repository;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.StringListConverter;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;

@Serdeable
@MappedEntity("payment")
public record PaymentData(
  @Id String id,
  String gateway,
  String gatewayId,
  String method,
  String nickname,
  String message,
  String recipientId,
  Amount amount,
  @Nullable String confirmation,
  @MappedProperty("cred_id") String gatewayCredentialId,
  @Nullable String goal,
  @Nullable Instant authorizationTimestamp,
  @DateCreated @Nullable Instant creationTimestamp,
  String status,
  @MappedProperty(converter = StringListConverter.class)
  List<String> attachments,
  @MappedProperty(type = DataType.JSON) @Nullable List<Action> actions,
  @MappedProperty(type = DataType.JSON) @Nullable Auction auction
) {
  public PaymentData withAuthorizationTimestamp(
    Instant newAuthorizationTimestamp
  ) {
    return new PaymentData(
      id,
      gateway,
      gatewayId,
      method,
      nickname,
      message,
      recipientId,
      amount,
      confirmation,
      gatewayCredentialId,
      goal,
      newAuthorizationTimestamp,
      creationTimestamp,
      status,
      attachments,
      actions,
      auction
    );
  }

  public PaymentData withStatus(String newStatus){
    return new PaymentData(
      id,
      gateway,
      gatewayId,
      method,
      nickname,
      message,
      recipientId,
      amount,
      confirmation,
      gatewayCredentialId,
      goal,
      authorizationTimestamp,
      creationTimestamp,
      newStatus,
      attachments,
      actions,
      auction
    );

  }

  @Serdeable
  public static record Action(String name, Map<String, Object> properties) {}

  @Serdeable
  public static record Auction(String item, Boolean isNew) {}
}

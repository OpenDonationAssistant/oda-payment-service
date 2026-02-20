package io.github.opendonationassistant.payment.repository;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.StringListConverter;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.Wither;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

@Serdeable
@MappedEntity("payment")
@Wither
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
  @Nullable Instant creationTimestamp,
  String status,
  @MappedProperty(converter = StringListConverter.class)
  List<String> attachments,
  @MappedProperty(type = DataType.JSON) List<Action> actions,
  @MappedProperty(type = DataType.JSON) @Nullable Auction auction
)
  implements PaymentDataWither {
  @Serdeable
  public static record Action(
    String id,
    String actionId,
    Integer amount,
    Map<String, Object> parameters
  ) {}

  @Serdeable
  public static record Auction(
    @Nullable String id,
    String item,
    Boolean isNew
  ) {}
}

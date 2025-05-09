package io.github.opendonationassistant.payment.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PaymentDataRepository
  extends CrudRepository<PaymentData, String> {
  List<
    PaymentData
  > getByRecipientIdAndStatusInOrderByAuthorizationTimestampDesc(
    String recipientId,
    List<String> statuses
  );
  Optional<PaymentData> getByGatewayId(String gatewayId);
  Optional<PaymentData> getById(String id);
}

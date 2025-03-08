package io.github.opendonationassistant.payment;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PaymentRepository extends CrudRepository<Payment, String> {
  List<Payment> getByRecipientIdAndStatusInOrderByAuthorizationTimestampDesc(
    String recipientId,
    List<String> statuses
  );
  Optional<Payment> getByGatewayId(String gatewayId);
}

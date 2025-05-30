package io.github.opendonationassistant.gateway.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface GatewayCredentialsDataRepository
  extends CrudRepository<GatewayCredentialsData, String> {
  public List<GatewayCredentialsData> findByRecipient(String recipient);
}

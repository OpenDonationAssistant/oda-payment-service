package io.github.opendonationassistant.recipient;

import java.util.List;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface GatewayCredentialsDataRepository extends CrudRepository<GatewayCredentialsData, String>  {
  public List<GatewayCredentialsData> findByRecipient(String recipient);
}

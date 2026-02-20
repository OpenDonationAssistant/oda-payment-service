package io.github.opendonationassistant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.gateway.command.DeleteGateway;
import io.github.opendonationassistant.gateway.command.DeleteGateway.DeleteGatewayCommand;
import io.github.opendonationassistant.gateway.command.SetGateway;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsDataRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class GatewaysTest {

  @Inject
  SetGateway setGateway;

  @Inject
  DeleteGateway deleteGateway;

  @Inject
  GatewayCredentialsDataRepository repository;

  @Test
  public void testDeleteGateway(@Given String recipientId) {
    var auth = mock(Authentication.class);
    when(auth.getAttributes()).thenReturn(
      Map.of("preferred_username", recipientId)
    );

    var createGatewayCommand = new SetGateway.SetGatewayCommand(
      "tempId",
      "gatewayId",
      "token",
      "gateway",
      "secret",
      "fiat",
      false
    );
    setGateway.setGateway(auth, createGatewayCommand);

    @NonNull
    final Optional<GatewayCredentialsData> shouldExist = repository.findById(
      "tempId"
    );
    assertTrue(shouldExist.isPresent());

    deleteGateway.deleteGateway(auth, new DeleteGatewayCommand("tempId"));
    assertTrue(repository.findById("tempId").isEmpty());
  }
}

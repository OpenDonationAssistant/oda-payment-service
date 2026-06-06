package io.github.opendonationassistant.gateway;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.gateway.repository.GatewayCredentialsData;
import io.github.opendonationassistant.gateway.repository.GatewayCredentialsDataRepository;
import io.github.opendonationassistant.gateway.repository.robokassa.Robokassa;
import io.github.opendonationassistant.gateway.repository.yookassa.YooKassa;
import io.github.opendonationassistant.gateway.repository.yoomoney.YooMoney;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Map;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone")
@ExtendWith(InstancioExtension.class)
public class AbstractGatewayRepositoryTest {

  @Inject
  AbstractGatewayRepository gatewayRepository;

  @Inject
  GatewayCredentialsDataRepository dataRepository;

  @Test
  public void testLoadingYookassaGateway() {
    dataRepository.save(
      new GatewayCredentialsData(
        "1",
        "testuser",
        "123123",
        "token",
        "yookassa",
        "",
        Map.of(),
        "fiat",
        true
      )
    );
    final Gateway gateway = gatewayRepository.get("testuser", "1");
    assertTrue(gateway instanceof YooKassa);
  }

  @Test
  public void testLoadingRobokassaGateway() {
    dataRepository.save(
      new GatewayCredentialsData(
        "1",
        "testuser",
        "123123",
        "token",
        "robokassa",
        "",
        Map.of(),
        "fiat",
        true
      )
    );
    final Gateway gateway = gatewayRepository.get("testuser", "1");
    assertTrue(gateway instanceof Robokassa);
  }

  @Test
  public void testLoadingYoomoneyGateway() {
    dataRepository.save(
      new GatewayCredentialsData(
        "1",
        "testuser",
        "123123",
        "token",
        "yoomoney",
        "",
        Map.of(),
        "fiat",
        true
      )
    );
    final Gateway gateway = gatewayRepository.get("testuser", "1");
    assertTrue(gateway instanceof YooMoney);
  }

  @Test
  public void testErrorWhenGatewayIsDisabled() {
    dataRepository.save(
      new GatewayCredentialsData(
        "1",
        "testuser",
        "123123",
        "token",
        "yoomoney",
        "",
        Map.of(),
        "fiat",
        false
      )
    );
    assertThrows(RuntimeException.class, () ->
      gatewayRepository.get("testuser", "1")
    );
  }
}

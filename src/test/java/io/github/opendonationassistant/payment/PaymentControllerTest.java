package io.github.opendonationassistant.payment;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.gateway.command.SetGateway;
import io.github.opendonationassistant.payment.commands.CreatePayment;
import io.github.opendonationassistant.payment.commands.CreatePayment.CreatePaymentResponse;
import io.github.opendonationassistant.payment.repository.InitedPayment;
import io.github.opendonationassistant.payment.view.PaymentController;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest(environments = "allinone")
public class PaymentControllerTest {

  private Logger log = LoggerFactory.getLogger(PaymentControllerTest.class);

  @Inject
  ApplicationContext context;

  @Inject
  SetGateway setGateway;

  @Inject
  CreatePayment createPayment;

  @Inject
  PaymentController paymentController;

  @WithSettings
  private final Settings settings = Settings.create()
    .mapType(Object.class, String.class);

  @Test
  public void testCreatingDraft(RequestSpecification spec) {
    var amount = new Amount(100, 0, "RUB");

    var request = new CreatePayment.CreatePaymentCommand(
      "id",
      "0",
      "testname",
      "message",
      "testuser",
      null,
      amount,
      List.of(),
      null,
      List.of(),
      null
    );

    final CreatePaymentResponse draft = createPayment
      .createDraft(request)
      .join();

    assertNotNull(draft.operationUrl());
    assertNotNull(draft.token());
    // TODO: attachments and money
    // TODO: check auth timestamp
  }

  @Test
  @Disabled
  public void testNotFoundForUnknownPaymentId(RequestSpecification spec) {
    spec
      .when()
      .header("Content-Type", "application/json")
      .get("/payments/unknownid")
      .then()
      .statusCode(404)
      .log()
      .ifError();
  }

  @Test
  @Disabled
  public void testReturnPaymentDraftByIdAfterCreation(
    RequestSpecification spec
  ) {
    var amount = new Amount(100, 0, "RUB");

    var request = new CreatePayment.CreatePaymentCommand(
      "id",
      "0",
      "testname",
      "message",
      "testuser",
      null,
      amount,
      List.of(),
      null,
      List.of(),
      null
    );

    String paymentId = spec
      .when()
      .body(request)
      .header("Content-Type", "application/json")
      .put("/payments/commands/create")
      .then()
      .statusCode(200)
      .extract()
      .jsonPath()
      .get("id");

    assertNotNull(paymentId);

    spec
      .when()
      .header("Content-Type", "application/json")
      .get("/payments/" + paymentId)
      .then()
      .log()
      .ifError()
      .statusCode(200)
      .body("id", notNullValue())
      .body("message", is("message"))
      .body("nickname", is("testname"))
      .body("recipientId", is("testuser"));
  }
}

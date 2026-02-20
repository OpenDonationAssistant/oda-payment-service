package io.github.opendonationassistant;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.gateway.command.SetGateway;
import io.github.opendonationassistant.payment.commands.CreatePayment;
import io.github.opendonationassistant.payment.commands.CreatePayment.CreatePaymentResponse;
import io.github.opendonationassistant.payment.view.PaymentController;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
public class PaymentsTest {

  @Inject
  SetGateway setGateway;

  @Inject
  CreatePayment createPayment;

  @Inject
  PaymentController paymentController;

  @Test
  public void testCreatingDraft(RequestSpecification spec) {
    var amount = new Amount(100, 0, "RUB");

    var request = new CreatePayment.CreatePaymentCommand(
      Generators.timeBasedEpochGenerator().generate().toString(),
      "0",
      "testname",
      "message",
      "testuser",
      "",
      amount,
      List.of(),
      null,
      List.of(),
      null,
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
    var paymentId = Generators.timeBasedEpochGenerator().generate().toString();

    var request = new CreatePayment.CreatePaymentCommand(
      paymentId,
      "0",
      "testname",
      "message",
      "testuser",
      "",
      amount,
      List.of(),
      null,
      List.of(),
      null,
      null
    );

    spec
      .when()
      .body(request)
      .header("Content-Type", "application/json")
      .put("/payments/commands/create")
      .then()
      .statusCode(200);

    spec
      .when()
      .header("Content-Type", "application/json")
      .get("/payments/" + paymentId)
      .then()
      .log()
      .ifError()
      .statusCode(200)
      .body("message", is("message"))
      .body("nickname", is("testname"))
      .body("recipientId", is("testuser"));
  }
}

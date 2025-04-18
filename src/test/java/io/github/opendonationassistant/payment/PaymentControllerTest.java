package io.github.opendonationassistant.payment;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.opendonationassistant.Beans;
import io.github.opendonationassistant.payment.amount.Amount;
import io.github.opendonationassistant.payment.commands.createpayment.CreatePaymentCommand;
import io.micronaut.context.ApplicationContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;

import java.util.List;

import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
public class PaymentControllerTest {

  @Inject
  ApplicationContext context;

  @Test
  public void testCreatingDraft(RequestSpecification spec) {
    Beans.context = context;
    var amount = new Amount(100, 0, "RUB");
    var request = new CreatePaymentCommand(
      "testname",
      "message",
      amount,
      "testuser",
      List.of()
    );

    // todo attachments and money

    // prettier-ignore
    spec
      .when()
        .header("Content-Type", "application/json")
        .body(request)
        .put("/payments/commands/create")
      .then()
        .log().all()
        .statusCode(200)
        .body("id", notNullValue())
        .body("message", is("message"))
        .body("nickname", is("testname"))
        .body("recipientId", is("testuser"))
        .body("confirmation", notNullValue());
    // prettier-ignore
  }

  @Test
  public void testNotFoundForUnknownPaymentId(RequestSpecification spec) {
    Beans.context = context;
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
  public void testReturnPaymentDraftByIdAfterCreation(
    RequestSpecification spec
  ) {
    Beans.context = context;
    var amount = new Amount(100, 0, "RUB");
    var request = new CreatePaymentCommand(
      null,
      "testname",
      "message",
      amount,
      "testuser",
      List.of()
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
      .log().ifError()
      .statusCode(200)
      .body("id", notNullValue())
      .body("message", is("message"))
      .body("nickname", is("testname"))
      .body("recipientId", is("testuser"));
  }
}

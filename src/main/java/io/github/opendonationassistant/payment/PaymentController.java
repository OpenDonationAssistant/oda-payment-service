package io.github.opendonationassistant.payment;

import io.github.opendonationassistant.recipient.GatewayCredentialsData;
import io.github.opendonationassistant.recipient.GatewayCredentialsDataRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class PaymentController {

  private final PaymentRepository payments;
  private final GatewayCredentialsDataRepository credentialsRepository;

  @Inject
  public PaymentController(
    PaymentRepository payments,
    GatewayCredentialsDataRepository credentialsRepository
  ) {
    this.payments = payments;
    this.credentialsRepository = credentialsRepository;
  }

  @Get("/payments/")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<List<Payment>> list(
    @NonNull Authentication auth,
    @Nullable @QueryValue("statuses") String statuses
  ) {
    final Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    List<String> statusFilter = StringUtils.isNotEmpty(statuses)
      ? Arrays.asList(statuses.split(","))
      : Arrays.asList("completed");
    List<Payment> completedPayments =
      payments.getByRecipientIdAndStatusInOrderByAuthorizationTimestampDesc(
        ownerId.get(),
        statusFilter
      );
    var page = completedPayments.size() > 10
      ? completedPayments.subList(0, 9)
      : completedPayments;
    return HttpResponse.ok(page);
  }

  @Get("/payments/{id}")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public HttpResponse<Payment> get(@PathVariable("id") String id) {
    return payments
      .findById(id)
      .map(HttpResponse::ok)
      .orElse(HttpResponse.notFound());
  }

  @Get("/payments/gateways")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public HttpResponse<List<GatewayData>> listGateways(
    @QueryValue("recipientId") String recipientId
  ) {
    return HttpResponse.ok(
      credentialsRepository
        .findByRecipient(recipientId)
        .stream()
        .map(GatewayCredentialsData::asGatewayData)
        .toList()
    );
  }

  @Serdeable
  public static record GatewayData(String id, String type, Boolean enabled) {}

  private Optional<String> getOwnerId(Authentication auth) {
    return Optional.ofNullable(
      auth.getAttributes().get("preferred_username")
    ).map(String::valueOf);
  }
}

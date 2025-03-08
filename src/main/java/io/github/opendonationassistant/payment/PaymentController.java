package io.github.opendonationassistant.payment;

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
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

@Controller("/payments")
public class PaymentController {

  private final PaymentRepository payments;

  @Inject
  public PaymentController(PaymentRepository payments) {
    this.payments = payments;
  }

  @Get
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<List<Payment>> list(
    @NonNull Authentication auth,
    @Nullable @QueryValue("statuses") String statuses
  ) {
    List<String> statusFilter = StringUtils.isNotEmpty(statuses)
      ? Arrays.asList(statuses.split(","))
      : Arrays.asList("completed");
    List<Payment> completedPayments =
      payments.getByRecipientIdAndStatusInOrderByAuthorizationTimestampDesc(
        getOwnerId(auth),
        statusFilter
      );
    var page = completedPayments.size() > 10
      ? completedPayments.subList(0, 9)
      : completedPayments;
    return HttpResponse.ok(page);
  }

  @Get("{id}")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public HttpResponse<Payment> get(@PathVariable("id") String id) {
    return payments
      .findById(id)
      .map(HttpResponse::ok)
      .orElse(HttpResponse.notFound());
  }

  private String getOwnerId(Authentication auth) {
    return String.valueOf(
      auth.getAttributes().getOrDefault("preferred_username", "")
    );
  }
}

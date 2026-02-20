package io.github.opendonationassistant.payment.view;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.payment.repository.Payment;
import io.github.opendonationassistant.payment.repository.PaymentData;
import io.github.opendonationassistant.payment.repository.PaymentData.Action;
import io.github.opendonationassistant.payment.repository.PaymentData.Auction;
import io.github.opendonationassistant.payment.repository.PaymentRepository;
import io.micronaut.core.annotation.NonNull;
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
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@Controller
public class PaymentController extends BaseController {

  private final PaymentRepository payments;

  @Inject
  public PaymentController(PaymentRepository payments) {
    this.payments = payments;
  }

  @Get("/payments/")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<List<PaymentDto>> list(
    @NonNull Authentication auth,
    @Nullable @QueryValue("statuses") String statuses
  ) {
    final Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    List<String> statusFilter = Optional.ofNullable(statuses)
      .map(it -> Arrays.asList(it.split(",")))
      .orElseGet(() -> List.of("completed"));
    List<Payment> completedPayments = payments.listByRecipientId(
      ownerId.get(),
      statusFilter
    );
    var page = completedPayments.size() > 10
      ? completedPayments.subList(0, 9)
      : completedPayments;
    return HttpResponse.ok(page.stream().map(this::convert).toList());
  }

  @Get("/payments/{id}")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public HttpResponse<PaymentDto> get(@PathVariable("id") String id) {
    return payments
      .getById(id)
      .map(this::convert)
      .map(HttpResponse::ok)
      .orElse(HttpResponse.notFound());
  }

  private PaymentDto convert(Payment payment) {
    final PaymentData data = payment.getData();
    return new PaymentDto(
      data.id(),
      data.gateway(),
      data.gatewayId(),
      data.method(),
      data.nickname(),
      data.message(),
      data.recipientId(),
      data.amount(),
      data.confirmation(),
      data.gatewayCredentialId(),
      data.goal(),
      data.authorizationTimestamp(),
      data.status(),
      data.attachments(),
      data.actions().stream().map(this::convert).toList(),
      convert(data.auction()).get()
    );
  }

  private Optional<PaymentDto.AuctionDto> convert(@Nullable Auction auction) {
    return Optional.ofNullable(auction).map(it ->
      new PaymentDto.AuctionDto(it.item(), it.isNew())
    );
  }

  private PaymentDto.ActionDto convert(Action action) {
    return new PaymentDto.ActionDto(
      action.id(),
      action.actionId(),
      action.parameters()
    );
  }

  @Serdeable
  public static record PaymentDto(
    String id,
    String gateway,
    String gatewayId,
    String method,
    String nickname,
    String message,
    String recipientId,
    Amount amount,
    @Nullable String confirmation,
    String gatewayCredentialId,
    @Nullable String goal,
    @Nullable Instant authorizationTimestamp,
    String status,
    List<String> attachments,
    List<ActionDto> actions,
    @Nullable AuctionDto auction
  ) {
    @Serdeable
    public static record ActionDto(
      String id,
      String actionId,
      Map<String, Object> parameters
    ) {}

    @Serdeable
    public static record AuctionDto(String item, Boolean isNew) {}
  }
}

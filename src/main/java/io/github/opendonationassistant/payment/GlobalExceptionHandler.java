package io.github.opendonationassistant.payment;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.zalando.problem.Problem;

@Singleton
public class GlobalExceptionHandler
  implements ExceptionHandler<RuntimeException, HttpResponse<Problem>> {

  private Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Override
  public HttpResponse<Problem> handle(
    HttpRequest request,
    RuntimeException exception
  ) {
    MDC.put("exception", exception.getMessage());
    log.error("Server Error");
    return HttpResponse.serverError(
      Problem.builder().withTitle("Internal Server Error").build()
    );
  }

  public static record ErrorMessage() {}
}

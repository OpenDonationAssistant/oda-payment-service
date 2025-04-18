package io.github.opendonationassistant.payment;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;

@Controller
public class GlobalErrorHandler {

  public HttpResponse<JsonError> error(HttpRequest<?> request, Throwable e) {
    JsonError error = new JsonError("Internal Server Error").link( // (2)
      Link.SELF,
      Link.of(request.getUri())
    );

    return HttpResponse.<JsonError>serverError().body(error); // (3)
  }
}

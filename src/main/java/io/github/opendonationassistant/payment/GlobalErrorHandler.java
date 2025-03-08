package io.github.opendonationassistant.payment;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;

@Controller
public class GlobalErrorHandler {

  // @Error(global = true) // (1)
  public HttpResponse<JsonError> error(HttpRequest<?> request, Throwable e) {
    JsonError error = new JsonError("Internal Server Error") // (2)
      .link(Link.SELF, Link.of(request.getUri()));

    return HttpResponse.<JsonError>serverError().body(error); // (3)
  }
}

package io.github.opendonationassistant.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

  public static String asJson(Object target) {
    var mapper = new ObjectMapper();
    var value = "can't be serialized as json";
    try {
      value = mapper.writeValueAsString(target);
    } catch (Exception e) {}
    return value;
  }
}

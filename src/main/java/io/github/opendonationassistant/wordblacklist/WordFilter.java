package io.github.opendonationassistant.wordblacklist;

import java.util.List;
import java.util.regex.Pattern;

public class WordFilter {

  public static final String regex =
    "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
  public static final Pattern pattern = Pattern.compile(regex);

  public List<String> banWords;

  public WordFilter(List<String> banWords) {
    this.banWords = banWords;
  }

  public String filter(String text) {
    for (String word : banWords) {
      text = text.replaceAll(word, "***");
    }
    text = pattern.matcher(text).replaceAll(" (ссылка удалена) ");
    text = text.replaceAll("https://", "").replaceAll("http://", "");
    return text;
  }
}

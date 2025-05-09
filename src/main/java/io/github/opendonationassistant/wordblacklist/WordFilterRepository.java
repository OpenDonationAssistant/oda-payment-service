package io.github.opendonationassistant.wordblacklist;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class WordFilterRepository {

  private WordFilterDataRepository dataRepository;
  private WordFilterData system;

  @Inject
  public WordFilterRepository(WordFilterDataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  public WordFilter getByRecipientId(String recipientId) {
    if (system == null) {
      system = dataRepository
        .getByRecipientId("system")
        .stream()
        .findFirst()
        .get();
    }
    final List<String> recipientWords = dataRepository
      .getByRecipientId(recipientId)
      .stream()
      .flatMap(data -> data.words().stream())
      .toList();
    final ArrayList<String> words = new ArrayList<>(system.words());
    words.addAll(recipientWords);
    return new WordFilter(words);
  }
}

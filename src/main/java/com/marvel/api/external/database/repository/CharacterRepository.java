package com.marvel.api.external.database.repository;

import com.marvel.api.external.database.document.CharacterDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CharacterRepository extends MongoRepository<CharacterDocument, String> {
  /**
   * Find character by Name
   *
   * @param name
   * @return character
   */
  List<CharacterDocument> findByNameIgnoreCaseContaining(String name);
}

package com.marvel.api.external.database.repository;

import com.marvel.api.external.database.document.CharacterDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharacterRepository extends MongoRepository<CharacterDocument, String> {
    /**
     * Find character by Name
     *
     * @param name
     * @return character
     */
    CharacterDocument findByNameIgnoreCaseContaining(String name);
}

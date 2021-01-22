package com.marvel.api.external.gateway;

import com.marvel.api.entity.Character;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CharacterGateway {
  List<Character> listAll();

  Optional<Character> listById(String id);

  Optional<Character> listByName(String name);

  Character save(@Valid Character character);

  Character update(String id, Character character);

  Character partialUpdate(String id, Map<String, Object> updates);

  void remove(String id);
}

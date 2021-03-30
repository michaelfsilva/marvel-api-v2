package com.marvel.api.service;

import com.marvel.api.entity.Character;
import com.marvel.api.external.gateway.CharacterGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CharacterService {
  private final CharacterGateway characterGateway;

  public List<Character> listAll() {
    return characterGateway.listAll();
  }

  public Optional<Character> listById(final String id) {
    return characterGateway.listById(id);
  }

  public List<Character> listByName(final String name) {
    return characterGateway.listByName(name);
  }

  public Character save(final Character character) {
    return characterGateway.save(character);
  }

  public Character update(final String id, final Character character) {
    return characterGateway.update(id, character);
  }

  public Character partialUpdate(final String id, final Map<String, Object> updates) {
    return characterGateway.partialUpdate(id, updates);
  }

  public void remove(final String id) {
    characterGateway.remove(id);
  }
}

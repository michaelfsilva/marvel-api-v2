package com.marvel.api.service;

import com.marvel.api.entity.Character;
import com.marvel.api.entity.response.Response;
import com.marvel.api.external.gateway.CharacterGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CharacterService {
  private final CharacterGateway characterGateway;

  public ResponseEntity<Response<List<Character>>> listAll() {
    final var characters = characterGateway.listAll();

    if (characters.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return ResponseEntity.ok(new Response<>(characters));
  }

  public ResponseEntity<Response<Character>> listById(final String id) {
    final var character = characterGateway.listById(id);

    return character
        .map(value -> ResponseEntity.ok(new Response<>(value)))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<Response<Character>> listByName(final String name) {
    final var character = characterGateway.listByName(name);

    return character
        .map(value -> ResponseEntity.ok(new Response<>(value)))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<Response<Character>> save(final Character character) {
    return new ResponseEntity<>(
        new Response<>(characterGateway.save(character)), HttpStatus.CREATED);
  }

  public ResponseEntity<Response<Character>> update(final String id, final Character character) {
    if (characterGateway.listById(id).isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(new Response<>(characterGateway.update(id, character)));
  }

  public ResponseEntity<Response<Character>> partialUpdate(
      final String id, final Map<String, Object> updates) {
    if (characterGateway.listById(id).isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(new Response<>(characterGateway.partialUpdate(id, updates)));
  }

  public ResponseEntity<Response<Integer>> remove(final String id) {
    if (characterGateway.listById(id).isEmpty()) {
      return new ResponseEntity<>(
          new Response<>("No character found for id: " + id), HttpStatus.NOT_FOUND);
    }

    characterGateway.remove(id);
    return ResponseEntity.ok(new Response<>("Character deleted"));
  }
}

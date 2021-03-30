package com.marvel.api.external.gateway.impl;

import com.marvel.api.entity.Character;
import com.marvel.api.external.database.document.CharacterDocument;
import com.marvel.api.external.database.repository.CharacterRepository;
import com.marvel.api.external.gateway.CharacterGateway;
import com.marvel.api.mapper.CharacterMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CharacterGatewayImpl implements CharacterGateway {

  private final CharacterRepository characterRepository;

  @Override
  public List<Character> listAll() {
    return CharacterMapper.toCharacterDocumentList(characterRepository.findAll());
  }

  @Override
  public Optional<Character> listById(final String id) {
    return Optional.of(
        CharacterMapper.fromCharacterDocument(
            characterRepository.findById(id).orElseThrow(IllegalArgumentException::new)));
  }

  @Override
  public List<Character> listByName(final String name) {
    return CharacterMapper.toCharacterDocumentList(
        characterRepository.findByNameIgnoreCaseContaining(name));
  }

  @Override
  public Character save(@Valid final Character character) {
    final CharacterDocument savedDocument =
        characterRepository.save(CharacterMapper.toCharacterDocument(character));
    return CharacterMapper.fromCharacterDocument(savedDocument);
  }

  @Override
  public Character update(final String id, final Character character) {
    final CharacterDocument characterDocument = CharacterMapper.toCharacterDocument(character);
    characterDocument.setId(id);
    final CharacterDocument savedDocument = characterRepository.save(characterDocument);
    return CharacterMapper.fromCharacterDocument(savedDocument);
  }

  @Override
  public Character partialUpdate(final String id, final Map<String, Object> updates) {
    final var character = listById(id).orElseThrow();

    updates.forEach(
        (key, value) -> {
          switch (key) {
            case "name":
              character.setName(value.toString());
              break;

            case "description":
              character.setDescription(value.toString());
              break;

            case "superPowers":
              character.setSuperPowers(value.toString());
              break;

            default:
          }
        });

    final CharacterDocument characterDocument = CharacterMapper.toCharacterDocument(character);
    characterDocument.setId(id);
    final CharacterDocument savedDocument = characterRepository.save(characterDocument);
    return CharacterMapper.fromCharacterDocument(savedDocument);
  }

  @Override
  public void remove(final String id) {
    characterRepository.deleteById(id);
  }
}

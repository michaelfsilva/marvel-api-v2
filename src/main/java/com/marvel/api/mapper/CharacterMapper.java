package com.marvel.api.mapper;

import com.marvel.api.entity.Character;
import com.marvel.api.entity.vo.request.CharacterRequest;
import com.marvel.api.entity.vo.response.CharacterResponse;
import com.marvel.api.external.database.document.CharacterDocument;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CharacterMapper {
  ModelMapper modelMapper = new ModelMapper();

  public Character fromCharacterDocument(final CharacterDocument characterDocument) {
    return modelMapper.map(characterDocument, Character.class);
  }

  public CharacterDocument toCharacterDocument(final Character character) {
    return modelMapper.map(character, CharacterDocument.class);
  }

  public List<Character> toCharacterDocumentList(
      final List<CharacterDocument> characterDocumentList) {
    return characterDocumentList.stream()
        .map(characterDocument -> modelMapper.map(characterDocument, Character.class))
        .collect(Collectors.toList());
  }

  public static Character fromCharacterRequest(final CharacterRequest characterRequest) {
    return modelMapper.map(characterRequest, Character.class);
  }

  public CharacterResponse toCharacterResponse(final Character character) {
    return modelMapper.map(character, CharacterResponse.class);
  }

  public List<CharacterResponse> toCharacterResponseList(final List<Character> characterList) {
    return characterList.stream()
        .map(character -> modelMapper.map(character, CharacterResponse.class))
        .collect(Collectors.toList());
  }
}

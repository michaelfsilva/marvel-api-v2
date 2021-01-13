package com.marvel.api.mapper;

import com.marvel.api.entity.Character;
import com.marvel.api.external.database.document.CharacterDocument;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CharacterMapper {
    ModelMapper modelMapper = new ModelMapper();

    public Character toCharacter(final CharacterDocument characterDocument) {
        return modelMapper.map(characterDocument, Character.class);
    }

    public CharacterDocument toCharacterDocument(final Character character) {
        return modelMapper.map(character, CharacterDocument.class);
    }

    public List<Character> toCharacterList(final List<CharacterDocument> characterDocumentList) {
        return characterDocumentList.stream()
                .map(characterDocument -> modelMapper.map(characterDocument, Character.class))
                .collect(Collectors.toList());
    }
}

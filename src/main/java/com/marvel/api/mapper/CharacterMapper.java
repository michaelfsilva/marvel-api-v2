package com.marvel.api.mapper;

import com.marvel.api.entity.Character;
import com.marvel.api.external.database.document.CharacterDocument;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

//@Mapper
@UtilityClass
public /*abstract */class CharacterMapper {
//    public static final CharacterMapper INSTANCE = Mappers.getMapper(CharacterMapper.class);

    ModelMapper modelMapper = new ModelMapper();

    public /*abstract*/ Character toCharacter(final CharacterDocument characterDocument) {
        return modelMapper.map(characterDocument, Character.class);
    }

    public /*abstract*/ CharacterDocument toCharacterDocument(final Character character) {
        return modelMapper.map(character, CharacterDocument.class);
    }

    public /*abstract*/ List<Character> toCharacterList(final List<CharacterDocument> characterDocumentList) {
        return characterDocumentList.stream()
                .map(characterDocument -> modelMapper.map(characterDocument, Character.class))
                .collect(Collectors.toList());
    }
}

package com.marvel.api.mapper;

import com.marvel.api.entity.Character;
import com.marvel.api.external.database.document.CharacterDocument;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class CharacterMapper {
    public static final CharacterMapper INSTANCE = Mappers.getMapper(CharacterMapper.class);

    public abstract Character toCharacter(final CharacterDocument characterDocument);

    public abstract CharacterDocument toCharacterDocument(final Character character);

    public abstract List<Character> toCharacterList(List<CharacterDocument> characterDocumentList);
}

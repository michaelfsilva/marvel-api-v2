package com.marvel.api.external.gateway;

import com.marvel.api.entity.Character;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

public interface CharacterGateway {
    List<Character> listAll();

    Character listById(String id);

    Character listByName(String name);

    Character add(@Valid Character character);

    Character update(@Valid Character character);

    Character partialUpdate(String id, Map<String, Object> updates);

    void remove(String id);
}

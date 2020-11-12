package com.marvel.api.service;

import com.marvel.api.entity.Character;
import com.marvel.api.entity.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class CharacterService {
    public ResponseEntity<Response<List<Character>>> listAll() {
        final List<Character> characters = characterService.listAll();

        if (characters.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(new Response<List<Character>>(characters));
    }
}

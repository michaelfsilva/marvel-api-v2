package com.marvel.api.controller;

import com.marvel.api.entity.vo.request.CharacterRequest;
import com.marvel.api.entity.vo.response.CharacterResponse;
import com.marvel.api.entity.vo.response.Response;
import com.marvel.api.mapper.CharacterMapper;
import com.marvel.api.service.CharacterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping({"/api", "/api/characters"})
public class CharacterController {

  private final CharacterService characterService;

  @ApiOperation(value = "Return Character data.")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 422, message = "Business Error"),
        @ApiResponse(code = 500, message = "Internal Server Error")
      })
  @GetMapping
  public ResponseEntity<Response<List<CharacterResponse>>> listAll() {
    log.debug("Listing all characters");
    final var characters = characterService.listAll();

    if (characters.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return ResponseEntity.ok(new Response<>(CharacterMapper.toCharacterResponseList(characters)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Response<CharacterResponse>> getById(@PathVariable final String id) {
    log.debug("Listing character by id");
    final var character = characterService.listById(id);

    return character
        .map(value -> ResponseEntity.ok(new Response<>(CharacterMapper.toCharacterResponse(value))))
        .orElseGet(
            () ->
                new ResponseEntity<>(
                    new Response<>("No character found for id: " + id), HttpStatus.NOT_FOUND));
  }

  @GetMapping("/findByName")
  public ResponseEntity<Response<List<CharacterResponse>>> getByName(
          @RequestParam final String name) {
    log.debug("Listing characters by name");
    final var characters = characterService.listByName(name);

    if (characters.isEmpty()) {
      return new ResponseEntity<>(
          new Response<>("No character found for name: " + name), HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(new Response<>(CharacterMapper.toCharacterResponseList(characters)));
  }

  @PostMapping(consumes = "application/json")
  public ResponseEntity<Response<CharacterResponse>> add(
      @Valid @RequestBody final CharacterRequest characterRequest, final BindingResult result) {
    log.debug("Adding a new character");

    if (result.hasErrors()) {
      final List<String> errors = new ArrayList<>();
      result.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
      return ResponseEntity.badRequest().body(new Response<>(errors));
    }

    return new ResponseEntity<>(
        new Response<>(
            CharacterMapper.toCharacterResponse(
                characterService.save(CharacterMapper.fromCharacterRequest(characterRequest)))),
        HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Response<CharacterResponse>> update(
      @PathVariable final String id,
      @Valid @RequestBody final CharacterRequest characterRequest,
      final BindingResult result) {
    log.debug("Updating a character by id: " + id);

    if (result.hasErrors()) {
      final List<String> errors = new ArrayList<>();
      result.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
      return ResponseEntity.badRequest().body(new Response<>(errors));
    }

    if (characterService.listById(id).isEmpty()) {
      return new ResponseEntity<>(
          new Response<>("No character found for id: " + id), HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(
        new Response<>(
            CharacterMapper.toCharacterResponse(
                characterService.update(
                    id, CharacterMapper.fromCharacterRequest(characterRequest)))));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Response<CharacterResponse>> patchUpdate(
      @PathVariable final String id, @RequestBody final Map<String, Object> updates) {
    log.debug("Partial updating a character by id: " + id);

    if (characterService.listById(id).isEmpty()) {
      return new ResponseEntity<>(
          new Response<>("No character found for id: " + id), HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(
        new Response<>(
            CharacterMapper.toCharacterResponse(characterService.partialUpdate(id, updates))));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Response<Integer>> remove(@PathVariable final String id) {
    log.debug("removing character with id: " + id);

    if (characterService.listById(id).isEmpty()) {
      return new ResponseEntity<>(
          new Response<>("No character found for id: " + id), HttpStatus.NOT_FOUND);
    }

    characterService.remove(id);
    return ResponseEntity.ok(new Response<>("Character deleted"));
  }
}

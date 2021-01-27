package com.marvel.api.controller;

import com.marvel.api.entity.Character;
import com.marvel.api.entity.response.Response;
import com.marvel.api.service.CharacterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/characters")
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
  public ResponseEntity<Response<List<Character>>> listAll() {
    log.debug("listing all characters");
    return characterService.listAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Response<Character>> getById(@PathVariable final String id) {
    log.debug("listing character by id");
    return characterService.listById(id);
  }

  @GetMapping("/findByName/{name}")
  public ResponseEntity<Response<Character>> getByName(@PathVariable final String name) {
    log.debug("listing character by name");
    return characterService.listByName(name);
  }

  @PostMapping(consumes = "application/json")
  public ResponseEntity<Response<Character>> add(
      @Valid @RequestBody final Character character, final BindingResult result) {
    if (result.hasErrors()) {
      final List<String> errors = new ArrayList<>();
      result.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
      return ResponseEntity.badRequest().body(new Response<>(errors));
    }

    return characterService.save(character);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Response<Character>> update(
      @PathVariable final String id,
      @Valid @RequestBody final Character character,
      final BindingResult result) {
    if (result.hasErrors()) {
      final List<String> errors = new ArrayList<>();
      result.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
      return ResponseEntity.badRequest().body(new Response<>(errors));
    }

    return characterService.update(id, character);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Response<Character>> patchUpdate(
      @PathVariable final String id, @RequestBody final Map<String, Object> updates) {
    return characterService.partialUpdate(id, updates);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Response<Integer>> remove(@PathVariable final String id) {
    log.debug("removing character with id: " + id);
    return characterService.remove(id);
  }
}

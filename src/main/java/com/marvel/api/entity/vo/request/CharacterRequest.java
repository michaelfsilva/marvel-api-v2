package com.marvel.api.entity.vo.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
public class CharacterRequest {
  @NotEmpty(message = "Name attribute is mandatory")
  private final String name;

  private final String description;
  private final String superPowers;
}

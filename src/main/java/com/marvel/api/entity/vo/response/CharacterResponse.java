package com.marvel.api.entity.vo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterResponse {
  private String id;
  private String name;
  private String description;
  private String superPowers;
}

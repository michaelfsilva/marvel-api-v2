package com.marvel.api.external.database.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;

@Document(collection = "Character")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CharacterDocument {
  @Id private String id;

  @NotEmpty(message = "Name attribute is mandatory")
  private String name;

  private String description;
  private String superPowers;
}

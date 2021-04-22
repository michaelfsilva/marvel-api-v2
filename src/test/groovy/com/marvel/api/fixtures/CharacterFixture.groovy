package com.marvel.api.fixtures

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.loader.TemplateLoader
import com.marvel.api.entity.Character

import static com.marvel.api.entity.Character.Fields.description
import static com.marvel.api.entity.Character.Fields.name
import static com.marvel.api.entity.Character.Fields.superPowers

class CharacterFixture implements TemplateLoader {
    public static final String BASE_CHARACTER = "base character"
    public static final String BASE_CHARACTER2 = "base character 2"
    public static final String EMPTY_CHARACTER = "empty character"

    @Override
    void load() {
        Fixture.of(Character).addTemplate(BASE_CHARACTER, new Rule() {
            {
                add(name, "Bucky")
                add(description, "The winter soldier")
                add(superPowers, "Strength, Steel arm")
            }
        })

        Fixture.of(Character).addTemplate(BASE_CHARACTER2, new Rule() {
            {
                add(name, "Capitain America")
                add(description, "The first avenger")
                add(superPowers, "Strength, Strong shield")
            }
        })

        Fixture.of(Character).addTemplate(EMPTY_CHARACTER, new Rule() {
            {
                add(name, "")
                add(description, "")
                add(superPowers, "")
            }
        })
    }
}

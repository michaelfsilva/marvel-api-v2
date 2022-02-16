package com.marvel.api.fixtures

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.loader.TemplateLoader
import com.marvel.api.entity.Character

import static com.marvel.api.entity.Character.Fields.*

class CharacterFixture implements TemplateLoader {
    public static final String BASE_CHARACTER = "base character"
    public static final String BASE_CHARACTER2 = "base character 2"
    public static final String EMPTY_CHARACTER = "empty character"

    @Override
    void load() {
        Fixture.of(Character).addTemplate(BASE_CHARACTER, new Rule() {
            {
                add(id, "620d3e2b7bfc7c7ebd5ce998")
                add(name, "Bucky")
                add(description, "The winter soldier")
                add(superPowers, "Strength, Steel arm")
            }
        })

        Fixture.of(Character).addTemplate(BASE_CHARACTER2, new Rule() {
            {
                add(id, "620c4c9442ba0b350522b31d")
                add(name, "Captain America")
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

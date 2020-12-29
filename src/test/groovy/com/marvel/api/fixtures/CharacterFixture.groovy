package com.marvel.api.fixtures

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.loader.TemplateLoader
import com.marvel.api.entity.Character

import static com.marvel.api.entity.Character.Fields.*

class CharacterFixture implements TemplateLoader {
    public static final String BASE_CHARACTER = "base character"
    public static final String EMPTY_CHARACTER = "empty character"

    @Override
    void load() {
        Fixture.of(Character).addTemplate(BASE_CHARACTER, new Rule() {
            {
                add(name, "Bucky")
                add(description, "Capitain's old friend")
                add(superPowers, "Strength, Steel arm")
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

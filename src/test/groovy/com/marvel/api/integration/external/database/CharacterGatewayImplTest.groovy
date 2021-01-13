package com.marvel.api.integration.external.database


import com.marvel.api.entity.Character
import com.marvel.api.external.database.document.CharacterDocument
import com.marvel.api.external.database.repository.CharacterRepository
import com.marvel.api.external.gateway.CharacterGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates

@SpringBootTest
class CharacterGatewayImplTest extends Specification {
    @Autowired
    CharacterGateway characterGateway
    @Autowired
    CharacterRepository characterRepository

    void setup() {
        loadTemplates("com.marvel.api.fixtures")

//        def characterDocument = CharacterMapper.toCharacterDocument(Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER))
//        characterRepository.save(characterDocument)
    }

    @Unroll
    def "Should save character on DB"() {
        given: "A valid character"
        def character = Character.builder().name(name).description(description).superPowers(superPowers).build();

        when: "Call the gateway"
        characterGateway.save(character)

        then: "Should be persisted on DB"
        // TODO check why it's not working
//        1 * characterRepository.save(any())

        and: "All fields should be persisted"
        CharacterDocument characterSaved = characterRepository.findByNameIgnoreCaseContaining(character.getName())
        characterSaved.name == character.name
        characterSaved.description == character.description
        characterSaved.superPowers == character.superPowers

        where: "Character is"
        name       | description                 | superPowers
        "Hulk"     | "The green gemn"            | "Strength, Resistance, Gama radiation"
        "Iron Man" | "A smart man with an armor" | "Flying, shot, laser, resistance"
        "Bucky"    | "Capitain's old friend"     | "Strength, Steel arm"
        "Thor"     | "The god of thunder"        | "Strength, Resistance, Thunder, Stormbraker"
    }

}

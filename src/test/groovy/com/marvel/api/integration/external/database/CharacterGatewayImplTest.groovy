package com.marvel.api.integration.external.database

import br.com.six2six.fixturefactory.Fixture
import com.marvel.api.entity.Character
import com.marvel.api.external.database.document.CharacterDocument
import com.marvel.api.external.database.repository.CharacterRepository
import com.marvel.api.external.gateway.CharacterGateway
import com.marvel.api.fixtures.CharacterFixture
import com.marvel.api.mapper.CharacterMapper
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

    CharacterDocument baseCharacter

    void setup() {
        loadTemplates("com.marvel.api.fixtures")

        baseCharacter = CharacterMapper.toCharacterDocument(Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER))
        baseCharacter.setId("1")
        characterRepository.save(baseCharacter)
    }

    @Unroll
    def "Should save character on DB"() {
        given: "A valid character"
        def character = Character.builder()
                .name(name)
                .description(description)
                .superPowers(superPowers)
                .build()

        when: "Call the gateway"
        characterGateway.save(character)

        then: "All fields should be persisted on DB"
        CharacterDocument characterSaved = characterRepository.findByNameIgnoreCaseContaining(character.getName())
        characterSaved.name == character.name
        characterSaved.description == character.description
        characterSaved.superPowers == character.superPowers

        where: "Character is"
        name       | description                 | superPowers
        "Hulk"     | "The green gemn"            | "Strength, Resistance, Gama radiation"
        "Iron Man" | "A smart man with an armor" | "Flying, shot, laser, resistance"
        "Thor"     | "The god of thunder"        | "Strength, Resistance, Thunder, Stormbraker"
    }

    def "Should list all character on DB"() {
        given: "Character already added on db"
        when: "Call the gateway"
        def characterList = characterGateway.listAll()

        then: "Characters should be returned properly"
        characterList.size() != 0
    }

    def "Should return character by id from DB"() {
        given: "Character already added on db"
        when: "Call the gateway"
        def character = characterGateway.listById("1")

        then: "Character should be returned properly"
        character != null
    }

    def "Should update character on DB"() {
        given: "Character with changes"
        def id = "1"
        Character character = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)
        character.setName("New name")
        character.setDescription("New description")
        character.setSuperPowers("New superpowers")

        when: "Call the gateway"
        characterGateway.update(id, character)

        then: "All fields should be persisted on DB"
        CharacterDocument characterSaved = characterRepository.findById(id).get()
        characterSaved.name == character.name
        characterSaved.description == character.description
        characterSaved.superPowers == character.superPowers

    }

    def "Should partial update a character on DB"() {
        given: "Changes to be applied to a character"
        def id = "1"
        def updates = new HashMap<String, String>()
        updates.put("description", "New description")
        updates.put("superPowers", "New superpowers")

        when: "Call the gateway"
        characterGateway.partialUpdate(id, updates)

        then: "All fields should be persisted on DB"
        CharacterDocument characterSaved = characterRepository.findById(id).get()
        characterSaved.name == baseCharacter.name
        characterSaved.description == updates.get("description")
        characterSaved.superPowers == updates.get("superPowers")
    }

    def "remove"() {
        given: "Character id to be removed"
        def id = "1"

        when: "Call the gateway"
        characterGateway.remove(id)

        then: "Characters should be removed properly"
        Optional<CharacterDocument> characterOptional = characterRepository.findById(id)
        characterOptional.isEmpty()
    }

}

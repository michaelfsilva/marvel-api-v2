package com.marvel.api.integration.external.database

import br.com.six2six.fixturefactory.Fixture
import com.marvel.api.entity.Character
import com.marvel.api.external.database.document.CharacterDocument
import com.marvel.api.external.database.repository.CharacterRepository
import com.marvel.api.external.gateway.CharacterGateway
import com.marvel.api.mapper.CharacterMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates

@SpringBootTest
//@ActiveProfiles("integration")
//@Transactional
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CharacterGatewayImplTest extends Specification {
    @Autowired
    CharacterRepository characterRepository
    @Autowired
    CharacterGateway characterGateway

    void setup() {
        loadTemplates("com.marvel.api.fixtures")

        def characterDocument = CharacterMapper.INSTANCE.toCharacterDocument(Fixture.from(Character).gimme(BASE_CHARACTER) as Character)
        characterRepository.save(characterDocument)
    }

    @Unroll
    def "Should save character on DB"() {
        given: "A valid character"
        def character = Character.builder().name(name).description(description).superPowers(superPowers).build();

        when: "Call the gateway"
        characterGateway.add(character)

        then: "Should be persisted on DB"
//        characterRepository.save(CharacterMapper.INSTANCE.toCharacterDocument(character));
        1 * characterRepository.save(any())
        CharacterDocument characterSaved = characterRepository.findByName(character.getName())

        and: "All fields should be persisted"
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

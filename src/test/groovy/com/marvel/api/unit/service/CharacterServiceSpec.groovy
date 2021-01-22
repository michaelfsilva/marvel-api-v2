package com.marvel.api.unit.service

import br.com.six2six.fixturefactory.Fixture
import com.marvel.api.entity.Character
import com.marvel.api.external.gateway.CharacterGateway
import com.marvel.api.fixtures.CharacterFixture
import com.marvel.api.mapper.CharacterMapper
import com.marvel.api.service.CharacterService
import org.springframework.http.HttpStatus
import spock.lang.Specification

import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates

class CharacterServiceSpec extends Specification {
    CharacterGateway characterGateway = Mock()
    CharacterService characterService = new CharacterService(characterGateway)

    void setup() {
        loadTemplates("com.marvel.api.fixtures")
    }

    def "Should return all characters"() {
        given: "characters mock"
        def charactersMock = new ArrayList<Character>()
        charactersMock.add(Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER))
        charactersMock.add(Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER2))

        when: "call the service"
        def response = characterService.listAll()

        then: "the gateway should be called"
        1 * characterGateway.listAll() >> charactersMock

        and: "characters should be returned"
        response.getBody().getData().size() == 2
    }

    def "Should not return characters"() {
        when: "call the service"
        def response = characterService.listAll()

        then: "the gateway should be called"
        1 * characterGateway.listAll() >> Arrays.asList()

        and: "response should be no content"
        response.getStatusCode() == HttpStatus.NO_CONTENT
    }

    def "Should list character by id"() {
        given: "character mock"
        def id = "1"
        Character characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)

        when: "call the service"
        def response = characterService.listById(id)

        then: "the gateway should be called"
        1 * characterGateway.listById(_) >> Optional.of(characterMock)

        and: "character should be returned"
        response.getBody().getData().name == characterMock.name
        response.getBody().getData().description == characterMock.description
        response.getBody().getData().superPowers == characterMock.superPowers
    }

    def "Should not return character by id"() {
        given: "character id"
        def id = "1"

        when: "call the service"
        def response = characterService.listById(id)

        then: "the gateway should be called"
        1 * characterGateway.listById(_) >> Optional.empty()

        and: "response should be not found"
        response.getStatusCode() == HttpStatus.NOT_FOUND
    }

    def "Should list character by name"() {
        given: "character mock"
        def characterMock = CharacterMapper.toCharacterDocument(Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER))

        when: "call the service"
        characterService.listByName(characterMock.name)

        then: "the gateway should be called"
        1 * characterGateway.listByName(_) >> characterMock
    }

    def "Should save a character"() {
        given: "character mock"
        def characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)

        when: "call the service"
        characterService.save(characterMock)

        then: "the gateway should be called"
        1 * characterGateway.save(_)
    }

    def "Should update character"() {
        given: "character mock"
        def id = "1"
        def characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)

        when: "call the service"
        characterService.update(id, characterMock as Character)

        then: "the gateway should be called"
        1 * characterGateway.update(_, _)
    }

    def "Should partial update character"() {
        given: "character id and updates"
        def id = "1"
        def updates = new HashMap<String, String>()

        when: "call the service"
        characterService.partialUpdate(id, updates)

        then: "the gateway should be called"
        1 * characterGateway.partialUpdate(_, _)
    }

    def "Should remove character"() {
        given: "character id"
        def id = "1"

        when: "call the service"
        characterService.remove(id)

        then: "the gateway should be called"
        1 * characterGateway.remove(_)
    }
}

package com.marvel.api.unit.controller

import br.com.six2six.fixturefactory.Fixture
import com.fasterxml.jackson.databind.ObjectMapper
import com.marvel.api.config.SecurityConstants
import com.marvel.api.controller.CharacterController
import com.marvel.api.entity.Character
import com.marvel.api.entity.vo.request.CharacterRequest
import com.marvel.api.fixtures.CharacterFixture
import com.marvel.api.mapper.CharacterMapper
import com.marvel.api.service.CharacterService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Files
import java.nio.file.Paths

import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

@WebMvcTest(controllers = [CharacterController])
class CharacterControllerSpec extends Specification {
    @Autowired
    MockMvc mockMvc

    @SpringBean
    CharacterService characterService = Mock()

    private String token
    private static final String PAYLOAD_RESPONSE_PATH = "src/test/resources/payloads/response/"

    void setup() {
        loadTemplates("com.marvel.api.fixtures")
        token = Jwts.builder()
                .setSubject("test")
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact()
    }

    def "Should return 403 when token is invalid"() {
        expect:
        mockMvc.perform(
                get("/api/characters").header("Authorization", "Bearer fakeToken")
        ).andExpect(MockMvcResultMatchers.status().isForbidden())
    }

    def "Should list all characters"() {
        given: "service return mock"
        List<Character> characters = Arrays.asList(
                Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER),
                Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER2)
        )

        and: "expected response"
        def response = new String(Files.readAllBytes(Paths.get(PAYLOAD_RESPONSE_PATH + "base_character_list.json")))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get("/api/characters")
                        .header("Authorization", "Bearer " + token)
        ).andReturn()

        then: "should call the service"
        1 * characterService.listAll() >> characters

        and: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == response
    }

    def "Should not list all characters"() {
        given: "service return mock"
        characterService.listAll() >> new ArrayList<>()

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get("/api/characters")
                        .header("Authorization", "Bearer " + token)
        ).andReturn()

        then: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.NO_CONTENT.value()
    }

    @Unroll
    def "Should list character by id #scenario"() {
        given: "service return mock"
        Character characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)
        characterService.listById(_ as String) >> {
            if (id == 1) {
                return Optional.of(characterMock)
            } else {
                return Optional.empty()
            }
        }

        and: "expected response"
        def response = expectedResponse.contains("json") ?
                new String(Files.readAllBytes(Paths.get(PAYLOAD_RESPONSE_PATH + expectedResponse))) : expectedResponse

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get(url, id)
                        .header("Authorization", "Bearer " + token)
        ).andReturn()

        then: "the return should have status #status"
        result.getResponse().getStatus() == statusExpected.value()

        and: "response #expectedResponse"
        result.getResponse().getContentAsString() == response

        where:
        scenario         | url                    | id || statusExpected       | expectedResponse
        "Bad url"        | "/v1/character{id}"    | 1  || HttpStatus.NOT_FOUND | ""
        "Nonexistent id" | "/api/characters/{id}" | 0  || HttpStatus.NOT_FOUND | "{\"data\":null,\"messages\":[\"No character found for id: 0\"]}"
        "Correct id"     | "/api/characters/{id}" | 1  || HttpStatus.OK        | "base_character.json"
    }

    def "Should list character by name"() {
        given: "service return mock"
        def name = "Bucky"
        def characterMock = Collections.singletonList(Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER))

        and: "expected response"
        def response = new String(Files.readAllBytes(Paths.get(PAYLOAD_RESPONSE_PATH + "base_character_single_list.json")))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get("/api/characters/findByName/{name}", name)
                        .header("Authorization", "Bearer " + token)
        ).andReturn()

        then: "should call the service"
        1 * characterService.listByName(_) >> characterMock

        and: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == response
    }

    @Unroll
    def "Should save character #scenario"() {
        given: "expected response"
        def response = expectedResponse.contains("json") ?
                new String(Files.readAllBytes(Paths.get(PAYLOAD_RESPONSE_PATH + expectedResponse))) : expectedResponse

        when: "call the api"
        MvcResult result = mockMvc.perform(
                post("/api/characters")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(character))
        ).andReturn()

        then: "should call the service"
        callsToService * characterService.save(_) >> {
            if (callsToService != 0) {
                CharacterMapper.fromCharacterRequest(character)
            }
        }

        and: "should return ok status"
        result.getResponse().getStatus() == expectedStatus.value()

        and: "response body"
        result.getResponse().getContentAsString() == response

        where:
        scenario                   | character                                       | callsToService | expectedStatus         | expectedResponse
        "Character name is null"   | CharacterRequest.builder().build()              | 0              | HttpStatus.BAD_REQUEST | "empty_name.json"
        "Character name is empty"  | CharacterRequest.builder().name("").build()     | 0              | HttpStatus.BAD_REQUEST | "empty_name.json"
        "Character name is filled" | CharacterRequest.builder().name("Test").build() | 1              | HttpStatus.CREATED     | "{\"data\":{\"name\":\"Test\",\"description\":null,\"superPowers\":null},\"messages\":null}"
    }

    def "Should update a character"() {
        given: "service return mock"
        def id = "1"
        CharacterRequest characterMock = CharacterRequest.builder()
                .name("Bucky")
                .description("The winter soldier")
                .superPowers("Strength, Steel arm")
                .build()

        and: "expected response"
        def response = new String(Files.readAllBytes(Paths.get(PAYLOAD_RESPONSE_PATH + "base_character.json")))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                put("/api/characters/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(characterMock))
        ).andReturn()

        then: "should call the service"
        1 * characterService.listById(_ as String) >> Optional.of(CharacterMapper.fromCharacterRequest(characterMock))
        1 * characterService.update(_ as String, _ as Character) >> CharacterMapper.fromCharacterRequest(characterMock)

        and: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == response
    }

    def "Should partial update a character"() {
        given: "character id, updates and mock"
        def id = "1"
        def updates = new HashMap<String, String>()
        def characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)

        and: "expected response"
        def response = new String(Files.readAllBytes(Paths.get(PAYLOAD_RESPONSE_PATH + "base_character.json")))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                patch("/api/characters/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updates))
        ).andReturn()

        then: "should call the service"
        1 * characterService.listById(_ as String) >> Optional.of(characterMock)
        1 * characterService.partialUpdate(_ as String, _ as Map<String, Object>) >> characterMock

        and: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == response
    }

    @Unroll
    def "Should delete a character #scenario"() {
        given: "service return mock"
        def characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)

        when: "call the api"
        MvcResult result = mockMvc.perform(
                delete("/api/characters/{id}", id)
                        .header("Authorization", "Bearer " + token)
        ).andReturn()

        then: "should call the service"
        1 * characterService.listById(_ as String) >> {
            if (id == 0) {
                Optional.empty()
            } else {
                Optional.of(characterMock)
            }
        }
        callsToRemove * characterService.remove(_ as String)

        and: "should return status"
        result.getResponse().getStatus() == statusExpected.value()

        and: "response body"
        result.getResponse().getContentAsString() == expectedResponse

        where:
        scenario                   | id || callsToRemove | statusExpected       | expectedResponse
        "Nonexistent character id" | 0  || 0             | HttpStatus.NOT_FOUND | "{\"data\":null,\"messages\":[\"No character found for id: 0\"]}"
        "Correct character id"     | 1  || 1             | HttpStatus.OK        | "{\"data\":null,\"messages\":[\"Character deleted\"]}"
    }
}

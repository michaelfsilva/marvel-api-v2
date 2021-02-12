package com.marvel.api.unit.controller

import br.com.six2six.fixturefactory.Fixture
import com.fasterxml.jackson.databind.ObjectMapper
import com.marvel.api.config.SecurityConstants
import com.marvel.api.controller.CharacterController
import com.marvel.api.entity.Character
import com.marvel.api.entity.response.Response
import com.marvel.api.fixtures.CharacterFixture
import com.marvel.api.service.CharacterService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
                get("/api/characters").header("authorization", "Bearer fakeToken")
        ).andExpect(MockMvcResultMatchers.status().isForbidden())
    }

    def "Should list all characters"() {
        given: "service return mock"
//        List<Character> characters = Arrays.asList(
//                Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER),
//                Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER2)
//        )
        Character characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)

        and: "expected response"
        def response = new String(Files.readAllBytes(Paths.get(PAYLOAD_RESPONSE_PATH + "base_character.json")))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get("/api/characters")
                        .header("authorization", "Bearer " + token)
        ).andReturn()

        then: "should call the service"
        1 * characterService.listAll() >> ResponseEntity.ok(new Response<>(characterMock))

        and: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == response
    }

    def "Should not list all characters"() {
        given: "service return mock"
        characterService.listAll() >> new ResponseEntity<>(HttpStatus.NO_CONTENT)

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get("/api/characters")
                        .header("authorization", "Bearer " + token)
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
                return ResponseEntity.ok(new Response<>(characterMock))
            } else {
                new ResponseEntity<>(
                        new Response<>("No character found for id: " + id), HttpStatus.NOT_FOUND)
            }
        }

        and: "expected response"
        def response = expectedResponse.contains("json") ?
                new String(Files.readAllBytes(Paths.get(PAYLOAD_RESPONSE_PATH + expectedResponse))) : expectedResponse

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get(url, id)
                        .header("authorization", "Bearer " + token)
        ).andReturn()

        then: "the return should have status #status"
        result.getResponse().getStatus() == statusExcpected.value()

        and: "response #expectedResponse"
        result.getResponse().getContentAsString() == response

        where:
        scenario         | url                    | id || statusExcpected      | expectedResponse
        "Bad url"        | "/v1/character{id}"    | 1  || HttpStatus.NOT_FOUND | ""
        "Nonexistent id" | "/api/characters/{id}" | 0  || HttpStatus.NOT_FOUND | "{\"data\":null,\"messages\":[\"No character found for id: 0\"]}"
        "Correct id"     | "/api/characters/{id}" | 1  || HttpStatus.OK        | "base_character.json"
    }

    def "Should list character by name"() {
        given: "service return mock"
        def name = "Bucky"
        def characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)

        and: "expected response"
        def response = new String(Files.readAllBytes(Paths.get(PAYLOAD_RESPONSE_PATH + "base_character.json")))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get("/api/characters/findByName/{name}", name)
                        .header("authorization", "Bearer " + token)
        ).andReturn()

        then: "should call the service"
        1 * characterService.listByName(_) >> ResponseEntity.ok(new Response<>(characterMock))

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
                        .header("authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(character))
        ).andReturn()

        then: "should call the service"
        callsToService * characterService.save(_) >> {
            if (callsToService != 0) {
                new ResponseEntity<>(
                        new Response<>(character), HttpStatus.CREATED)
            }
        }

        and: "should return ok status"
        result.getResponse().getStatus() == expectedStatus.value()

        and: "response body"
        result.getResponse().getContentAsString() == response

        where:
        scenario                   | character                                | callsToService | expectedStatus         | expectedResponse
        "character name is null"   | Character.builder().build()              | 0              | HttpStatus.BAD_REQUEST | "empty_name.json"
        "character name is empty"  | Character.builder().name("").build()     | 0              | HttpStatus.BAD_REQUEST | "empty_name.json"
        "character name is filled" | Character.builder().name("Test").build() | 1              | HttpStatus.CREATED     | "{\"data\":{\"name\":\"Test\",\"description\":null,\"superPowers\":null},\"messages\":null}"
    }

    def "Should update a character"() {
        given: "service return mock"
        def id = "1"
        Character characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)

        and: "expected response"
        def response = new String(Files.readAllBytes(Paths.get(PAYLOAD_RESPONSE_PATH + "base_character.json")))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                put("/api/characters/{id}", id)
                        .header("authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(characterMock))
        ).andReturn()

        then: "should call the service"
        1 * characterService.update(_ as String, _ as Character) >> ResponseEntity.ok(new Response<>(characterMock))

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
                        .header("authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updates))
        ).andReturn()

        then: "should call the service"
        1 * characterService.partialUpdate(_ as String, _ as Map<String, Object>) >> ResponseEntity.ok(new Response<>(characterMock))

        and: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == response
    }

    def "Should delete a character"() {
        when: "call the api"
        MvcResult result = mockMvc.perform(
                delete("/api/characters/{id}", 1)
                        .header("authorization", "Bearer " + token)
        ).andReturn()

        then: "should call the service"
        1 * characterService.remove(_ as String) >> ResponseEntity.ok(new Response<>("Character deleted"))

        and: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == "{\"data\":null,\"messages\":[\"Character deleted\"]}"
    }
}

package com.marvel.api.unit.controller

import br.com.six2six.fixturefactory.Fixture
import com.fasterxml.jackson.databind.ObjectMapper
import com.marvel.api.controller.CharacterController
import com.marvel.api.entity.Character
import com.marvel.api.entity.response.Response
import com.marvel.api.fixtures.CharacterFixture
import com.marvel.api.service.CharacterService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification
import spock.lang.Unroll

import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

@WebMvcTest(controllers = [CharacterController])
@AutoConfigureMockMvc
class CharacterControllerSpec extends Specification {
    @Autowired
    MockMvc mockMvc

    @SpringBean
    CharacterService characterService = Mock()

    private String user = "test"
    private String pass = "marvel"

    void setup() {
        loadTemplates("com.marvel.api.fixtures")
    }

    def "Should return 401 when credentials are incorrect"() {
        expect:
        mockMvc.perform(
                get("/api/characters")
                        .with(httpBasic("1", "1"))
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    def "Should list all characters"() {
        given: "service return mock"
        List<Character> characters = Arrays.asList(
                Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER),
                Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER2)
        )
        characterService.listAll() >> ResponseEntity.ok(new Response<>(characters))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get("/api/characters")
                        .with(httpBasic(user, pass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        then: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == ""
    }

    def "Should not list all characters"() {
        given: "service return mock"
        characterService.listAll() >> new ResponseEntity<>(HttpStatus.NO_CONTENT)

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get("/api/characters")
                        .with(httpBasic(user, pass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        then: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.NO_CONTENT.value()

        and: "response body"
        result.getResponse().getContentAsString() == ""
    }

    @Unroll
    def "Should list character by id #scenario"() {
        given: "service return mock"
        def characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)
        characterService.remove(_ as String) >> {
            if (id == 1) {
                return ResponseEntity.ok(new Response<>(characterMock))
            } else {
                new ResponseEntity<>(HttpStatus.NOT_FOUND)
            }
        }

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get(url, id)
                        .with(httpBasic(user, pass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        then: "the return should have status #status"
        result.getResponse().getStatus() == status.value()

        and: "response #expectedResponse"
        result.getResponse().getContentAsString() == expectedResponse

        where:
        scenario         | url                    | id || status               | expectedResponse
        "Bad url"        | "/v1/character{id}"    | 1  || HttpStatus.NOT_FOUND | ""
        "Nonexistent id" | "/api/characters/{id}" | 0  || HttpStatus.NOT_FOUND | "{\"data\":null,\"messages\":[\"No character found for id: 0\"]}"
        "Correct id"     | "/api/characters/{id}" | 1  || HttpStatus.OK        | "{\"data\":null,\"messages\":[\"Character deleted\"]}"
    }

    def "Should list character by name"() {
        given: "service return mock"
        def name = "Bucky"
        def characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)
        characterService.listAll() >> ResponseEntity.ok(new Response<>(characterMock))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                get("/findByName/{name}", name)
                        .with(httpBasic(user, pass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        then: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == ""
    }

    def "Should save character"() {
        given: "service return mock"
        def characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)
        characterService.save() >> new ResponseEntity<>(
                new Response<>(characterMock), HttpStatus.CREATED)

        when: "call the api"
        MvcResult result = mockMvc.perform(
                post("/api/characters")
                        .with(httpBasic(user, pass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        then: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.CREATED.value()

        and: "response body"
        result.getResponse().getContentAsString() == ""
    }

    def "Should update a character"() {
        given: "service return mock"
        def id = "1"
        Character characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)
        characterMock.name = "Test Name"
        characterMock.description = "Test Description"
        characterService.update(_ as String, _ as Character) >> ResponseEntity.ok(new Response<>(characterMock))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                post("/api/characters/{id}", id)
                        .with(httpBasic(user, pass))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(characterMock))
        ).andReturn()

        then: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == ""
    }

    def "Should partial update a character"() {
        given: "character id, updates and mock"
        def id = "1"
        def updates = new HashMap<String, String>()
        def characterMock = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)
        characterService.partialUpdate(_ as String, _ as Map<String, Object>) >> ResponseEntity.ok(new Response<>(characterMock))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                patch("/api/characters/{id}", id)
                        .with(httpBasic(user, pass))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updates))
        ).andReturn()

        then: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()

        and: "response body"
        result.getResponse().getContentAsString() == ""
    }

    def "Should delete a character"() {
        given: "service return mock"
        characterService.remove(_ as String) >> ResponseEntity.ok(new Response<>("Character deleted"))

        when: "call the api"
        MvcResult result = mockMvc.perform(
                delete("/api/characters/{id}", 1)
                        .with(httpBasic(user, pass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()

        then: "should return ok status"
        result.getResponse().getStatus() == HttpStatus.OK.value()
    }
}

package com.marvel.api.component

import br.com.six2six.fixturefactory.Fixture
import com.marvel.api.config.SecurityConstants
import com.marvel.api.entity.Character
import com.marvel.api.entity.vo.response.CharacterResponse
import com.marvel.api.external.database.document.CharacterDocument
import com.marvel.api.external.database.repository.CharacterRepository
import com.marvel.api.fixtures.CharacterFixture
import com.marvel.api.mapper.CharacterMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.restassured.RestAssured
import io.restassured.http.Header
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import spock.lang.Specification

import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates
import static io.restassured.RestAssured.expect
import static io.restassured.RestAssured.given
import static io.restassured.http.ContentType.JSON
import static org.hamcrest.CoreMatchers.equalTo

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
@RunWith(SpringRunner.class)
class CharacterComponentSpec extends Specification {
    @Autowired
    CharacterRepository characterRepository

    @Value('${local.server.port}')
    private int port

    private String token
    private String endpoint = "/api/characters"
    private Header header
    private CharacterDocument baseCharacter
    private CharacterDocument baseCharacter2

    void setup() {
        loadTemplates("com.marvel.api.fixtures")

        RestAssured.port = port

        token = Jwts.builder()
                .setSubject("test")
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact()

        header = new Header("Authorization", "Bearer " + token)

        baseCharacter = CharacterMapper.toCharacterDocument(Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER))
        baseCharacter2 = CharacterMapper.toCharacterDocument(Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER2))
        baseCharacter.setId("1")
        baseCharacter2.setId("2")
        characterRepository.saveAll(Arrays.asList(baseCharacter, baseCharacter2))
    }

    def "Should list all characters"() {
        given: "request"
        def request = given()
                .contentType(JSON)
                .header(header)
                .log().all()

        when: "send a get request"
        final response = request.get(endpoint)

        then: "response status should be ok"
        response.statusCode() == HttpStatus.OK.value()

        and: "should return a list of characters"
        !response.jsonPath().getList("data").isEmpty()

        and: "result should match"
        CharacterResponse character = response.jsonPath().getList("data").get(0) as CharacterResponse
        CharacterResponse character2 = response.jsonPath().getList("data").get(1) as CharacterResponse
        character.getName() == baseCharacter.getName()
        character.getDescription() == baseCharacter.getDescription()
        character.getSuperPowers() == baseCharacter.getSuperPowers()
        character2.getName() == baseCharacter2.getName()
        character2.getDescription() == baseCharacter2.getDescription()
        character2.getSuperPowers() == baseCharacter2.getSuperPowers()
    }

    def "Should list character by id"() {
        given: "request"
        def request = given()
                .contentType(JSON)
                .header(header)

        when: "send a get request"
        final response = request.get(endpoint + "/{id}", [id: "1"])

        then: "response status should be ok"
        response.statusCode() == HttpStatus.OK.value()

        and: "should return a character"
        CharacterResponse character = response.jsonPath().getMap("data") as CharacterResponse
        character.getName() == baseCharacter.getName()
        character.getDescription() == baseCharacter.getDescription()
        character.getSuperPowers() == baseCharacter.getSuperPowers()
    }

    def "Should list characters by name"() {
        given: "request"
        def request = given()
                .contentType(JSON)
                .header(header)

        when: "send a get request"
        final response = request.get(endpoint + "/findByName/{name}", [name: "captain"])

        then: "response status should be ok"
        response.statusCode() == HttpStatus.OK.value()

        and: "should return a character"
        CharacterResponse character = response.jsonPath().getList("data").get(0) as CharacterResponse
        character.getName() == baseCharacter2.getName()
        character.getDescription() == baseCharacter2.getDescription()
        character.getSuperPowers() == baseCharacter2.getSuperPowers()
    }

    def "Should save a character"() {
        given: "request"
        def request = given()
                .contentType(JSON)
                .header(header)
                .body(baseCharacter)

        when: "send a post request"
        final response = request.post(endpoint)

        then: "response status should be created"
        response.statusCode() == HttpStatus.CREATED.value()

        and: "should return the character created"
        CharacterResponse character = response.jsonPath().getMap("data") as CharacterResponse
        character.getName() == baseCharacter.getName()
        character.getDescription() == baseCharacter.getDescription()
        character.getSuperPowers() == baseCharacter.getSuperPowers()
    }

    def "Should update a character"() {
        given: "request and character"
        def characterRequest = baseCharacter
        characterRequest.setName("Test")

        def request = given()
                .contentType(JSON)
                .header(header)
                .body(characterRequest)

        when: "send a put request"
        final response = request.put(endpoint + "/{id}", [id: "1"])

        then: "response status should be ok"
        response.statusCode() == HttpStatus.OK.value()

        and: "should return the character updated"
        CharacterResponse character = response.jsonPath().getMap("data") as CharacterResponse
        character.getName() == characterRequest.getName()
        character.getDescription() == characterRequest.getDescription()
        character.getSuperPowers() == characterRequest.getSuperPowers()
    }

    def "Should update partially a character"() {
        given: "request and character"
        def updates = Map.of(
                "description", "Test Description",
                "superPowers", "Super Test")
        def request = given()
                .contentType(JSON)
                .header(header)
                .body(updates)

        when: "send a patch request"
        final response = request.patch(endpoint + "/{id}", [id: "1"])

        then: "response status should be ok"
        response.statusCode() == HttpStatus.OK.value()

        and: "should return the character updated"
        CharacterResponse character = response.jsonPath().getMap("data") as CharacterResponse
        character.getName() == baseCharacter.getName()
        character.getDescription() == updates["description"]
        character.getSuperPowers() == updates["superPowers"]
    }

    def "Should delete a character"() {
        given:
        given()
                .header(header)
                .when()
                .delete(endpoint + "/{id}", [id: "1"])
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .spec(
                        expect()
                                .body("messages[0]", equalTo("Character deleted")))
    }
}

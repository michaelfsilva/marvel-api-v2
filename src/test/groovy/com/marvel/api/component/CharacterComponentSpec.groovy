package com.marvel.api.component


import com.marvel.api.config.SecurityConstants
import com.marvel.api.entity.vo.request.CharacterRequest
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.restassured.RestAssured
import io.restassured.http.Header
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import spock.lang.Ignore
import spock.lang.Specification

import static io.restassured.RestAssured.expect
import static io.restassured.RestAssured.given
import static io.restassured.http.ContentType.JSON

//TODO
// remove the @Ignore

@Ignore
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CharacterComponentSpec extends Specification {

    @Value('${local.server.port}')
    private int port

    private String token
    private String endpoint = "/api/characters"
    private Header header

    void setup() {
//        loadTemplates("com.marvel.api.fixtures")

        RestAssured.port = port

        token = Jwts.builder()
                .setSubject("test")
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact()

        header = new Header("Authorization", "Bearer " + token)
    }

    def "Should list all characters"() {
        given:
        def request = given()
                .contentType(JSON)
                .header(header)
//                .log().all()

        when:
        final response = request.get(endpoint)

        then:
        response.statusCode() == HttpStatus.NO_CONTENT.value()
    }

    def "Should list character by id"() {
        given:
        def request = given()
                .contentType(JSON)
                .header(header)

        when:
        final response = request.get(endpoint + "/{id}", [id: "1"])

        then:
        response.statusCode() == HttpStatus.NO_CONTENT.value()
    }

    def "Should list characters by name"() {
        given:
        def request = given()
                .contentType(JSON)
                .header(header)

        when:
        final response = request.get(endpoint + "/{name}", [name: "Thor"])

        then:
        response.statusCode() == HttpStatus.NO_CONTENT.value()
    }

    def "Should save a character"() {
        given:
//        def character = Fixture.from(Character).gimme(CharacterFixture.BASE_CHARACTER)
        def character = CharacterRequest.builder().name("Test").build()

        def request = given()
                .contentType(JSON)
                .header(header)
                .body(character)

        when:
        final response = request.post(endpoint)

        then:
        response.statusCode() == HttpStatus.CREATED.value()
    }

    def "Should update a character"() {

    }

    def "Should update partially a character"() {

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
                                .header("content-type", equals("application/json"))
                                .body("message", hasItem("Character deleted")))
    }
}

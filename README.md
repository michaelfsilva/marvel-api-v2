# marvel-api-v2
New version of Marvel API to provide Marvel Comics data

## Instructions to use
Download the zip project by clicking on Clone or download button, then Download ZIP;  
Extract the zip file and open a terminal in the folder extracted;  
Run the following command:  

If you have gradle installed:
```
$ gradle bootRun
```
If not:
```
$ java -jar marvel-api-0.0.1-SNAPSHOT.jar
```

Access http://localhost:8080/v1/characters/ with user "test" and password "marvel" to see a list of characters.

##API Documentation

http://localhost:8080/v2/api-docs  
http://localhost:8080/swagger-ui.html

## APIs endpoints
GET http://localhost:8080/v1/characters/ [list all characters]  
GET http://localhost:8080/v1/characters/{id} [list a character by ID]  
GET http://localhost:8080/v1/characters/findByName/{name} [list a character by name ignoring case]  
POST http://localhost:8080/v1/characters/ [add a new character]  
PUT http://localhost:8080/v1/characters/{id} [update character all attributes]  
PATCH http://localhost:8080/v1/characters/{id} [update one or more attributes of a character]  
DELETE http://localhost:8080/v1/characters/{id} [remove a character]  

## Commands
- Set Git Pre-Push Hook to execute unit and component tests before every git push

	`./gradlew installGitHooks`
	
- Build project

    `./gradlew clean build`

- Test project

    `./gradlew test`

- Start project

    `./gradlew bootRun`

- Start project with local profile

    `./gradlew bootRun -Penv=local`

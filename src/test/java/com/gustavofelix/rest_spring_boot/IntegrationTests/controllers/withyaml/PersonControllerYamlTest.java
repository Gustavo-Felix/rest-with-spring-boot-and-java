package com.gustavofelix.rest_spring_boot.IntegrationTests.controllers.withyaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.gustavofelix.rest_spring_boot.IntegrationTests.dto.PersonDTO;
import com.gustavofelix.rest_spring_boot.IntegrationTests.testcontainers.AbstractIntegrationTest;
import com.gustavofelix.rest_spring_boot.config.TestConfigs;
import com.gustavofelix.rest_spring_boot.serialization.converter.YamlJackson2HttpMessageConverter;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;

    private static PersonDTO person;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        RestAssured.config = RestAssured.config()
                .encoderConfig(
                        EncoderConfig.encoderConfig()
                                .encodeContentTypeAs("application/x-yaml", ContentType.TEXT)
                );

        person = new PersonDTO();
    }

    private void mockPerson() {
        person.setFirstName("Linus");
        person.setLastName("Torvalds");
        person.setAddress("Helsinki - Finland");
        person.setGender("Male");
        person.setEnabled(true);
    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)
                .addHeader("Accept", "application/x-yaml")
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                    .body(objectMapper.writeValueAsString(person))
                .when()
                    .post()
                .then()
                    .statusCode(201)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .extract()
                    .body()
                        .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());

    }

    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        person.setLastName("Benedict Torvalds");

        var content = given(specification)
                .contentType("application/x-yaml")
                    .pathParam("id", person.getId())
                    .body(objectMapper.writeValueAsString(person))
                .when()
                    .put("{id}")
                .then()
                    .statusCode(200)
                .contentType("application/x-yaml")
                .extract()
                    .body()
                        .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());

    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {

        var content = given(specification)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .extract()
                .body()
                .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());
    }

    @Test
    @Order(4)
    void disableTest() throws JsonProcessingException {

        var content = given(specification)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                    .pathParam("id", person.getId())
                .when()
                    .patch("{id}")
                .then()
                    .statusCode(200)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .extract()
                    .body()
                        .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertFalse(createdPerson.getEnabled());
    }

    @Test
    @Order(5)
    void deleteTest() throws JsonProcessingException {

        given(specification)
                .pathParam("id", person.getId())
            .when()
                .delete("{id}")
            .then()
                .statusCode(204);

    }

    @Test
    @Order(6)
    void findAllTest() throws JsonProcessingException {

        var content = given(specification)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .extract()
                    .body()
                        .asString();

        List<PersonDTO> people = objectMapper.readValue(
                content,
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, PersonDTO.class)
        );

        PersonDTO personOne = people.getFirst();
        PersonDTO personTwo = people.get(1);
        person = people.getFirst();

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Gustavo", personOne.getFirstName());
        assertEquals("Camargo", personOne.getLastName());
        assertEquals("SP", personOne.getAddress());
        assertEquals("Male", personOne.getGender());
        assertTrue(personOne.getEnabled());

        assertNotNull(personTwo.getId());
        assertTrue(personTwo.getId() > 0);

        assertEquals("Bruna", personTwo.getFirstName());
        assertEquals("Camargo", personTwo.getLastName());
        assertEquals("SP", personTwo.getAddress());
        assertEquals("Female", personTwo.getGender());
        assertTrue(personTwo.getEnabled());

    }
}
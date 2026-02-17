package com.gustavofelix.rest_spring_boot.IntegrationTests.controllers.cors.withyaml;

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


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlCorsTest extends AbstractIntegrationTest {

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
        person.setFirstName("Gustavo");
        person.setLastName("Camargo");
        person.setAddress("Sorocaba - São Paulo - BR");
        person.setGender("Male");
        person.setEnabled(true);
    }

    @Test
    @Order(1)
    void insert() throws JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)
                .addHeader("Accept", YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType("application/x-yaml")
                    .body(objectMapper.writeValueAsString(person))
                .when()
                    .post()
                .then()
                    .statusCode(201)
                .extract()
                    .body()
                    .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertNotNull(createdPerson.getFirstName());
        assertNotNull(createdPerson.getLastName());
        assertNotNull(createdPerson.getAddress());
        assertNotNull(createdPerson.getGender());

        assertTrue(createdPerson.getId() > 0);

        assertEquals("Gustavo", createdPerson.getFirstName());
        assertEquals("Camargo", createdPerson.getLastName());
        assertEquals("Sorocaba - São Paulo - BR", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
    }

    @Test
    @Order(2)
    void createWithWrongOrigin() throws JsonProcessingException {
        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_TEST)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType("application/x-yaml")
                    .body(objectMapper.writeValueAsString(person))
                .when()
                    .post()
                .then()
                    .statusCode(403)
                .extract()
                    .body()
                        .asString();

        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(3)
    void findById() throws JsonProcessingException {
        person.setId(1L);

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)
                .addHeader("Accept", YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType("application/x-yaml")
                .pathParam("id", person.getId())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertNotNull(createdPerson.getFirstName());
        assertNotNull(createdPerson.getLastName());
        assertNotNull(createdPerson.getAddress());
        assertNotNull(createdPerson.getGender());

        assertTrue(createdPerson.getId() > 0);

        assertEquals(1L, createdPerson.getId());
        assertEquals("Gustavo", createdPerson.getFirstName());
        assertEquals("Camargo", createdPerson.getLastName());
        assertEquals("SP", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertEquals(true, createdPerson.getEnabled());

    }

    @Test
    @Order(4)
    void findByIdWithWrongOrigin() {
        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_TEST)
                .addHeader("Accept", YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType("application/x-yaml")
                    .pathParam("id", person.getId())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(403)
                .extract()
                    .body()
                        .asString();

        assertEquals("Invalid CORS request", content);
    }
}
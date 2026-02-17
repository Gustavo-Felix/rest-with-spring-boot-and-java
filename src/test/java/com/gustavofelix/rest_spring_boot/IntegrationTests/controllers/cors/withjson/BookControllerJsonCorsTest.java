package com.gustavofelix.rest_spring_boot.IntegrationTests.controllers.cors.withjson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gustavofelix.rest_spring_boot.IntegrationTests.dto.BookDTO;
import com.gustavofelix.rest_spring_boot.IntegrationTests.testcontainers.AbstractIntegrationTest;
import com.gustavofelix.rest_spring_boot.config.TestConfigs;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerJsonCorsTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static BookDTO book;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookDTO();
    }

    private void mockBooks() {
        book.setAuthor("Test Author");
        book.setLaunchDate(Instant.parse("2007-12-03T10:15:30.00Z"));
        book.setPrice(180.00);
        book.setTitle("Book For Test");
    }

    @Test
    @Order(1)
    void insert() throws IOException {
        mockBooks();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(book)
                .when()
                    .post()
                .then()
                    .statusCode(201)
                .extract()
                    .body()
                    .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertNotNull(createdBook.getAuthor());
        assertNotNull(createdBook.getTitle());
        assertNotNull(createdBook.getLaunchDate());
        assertNotNull(createdBook.getPrice());

        assertTrue(createdBook.getId() > 0);

        assertEquals("Test Author", createdBook.getAuthor());
        assertEquals(Instant.parse("2007-12-03T10:15:30.00Z"), createdBook.getLaunchDate());
        assertEquals(180.00, createdBook.getPrice());
        assertEquals("Book For Test", createdBook.getTitle());
    }

    @Test
    @Order(2)
    void createWithWrongOrigin() throws IOException {
        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_TEST)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(book)
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
    void findById() throws IOException {
        book.setId(1L);

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", book.getId())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertNotNull(createdBook.getAuthor());
        assertNotNull(createdBook.getTitle());
        assertNotNull(createdBook.getLaunchDate());
        assertNotNull(createdBook.getPrice());

        assertTrue(createdBook.getId() > 0);

        assertEquals(1L, createdBook.getId());
        assertEquals("Michael C. Feathers", createdBook.getAuthor());
        assertEquals(Instant.parse("2017-11-29T13:50:05.878Z"), createdBook.getLaunchDate());
        assertEquals(49.00, createdBook.getPrice());
        assertEquals("Working effectively with legacy code", createdBook.getTitle());

    }

    @Test
    @Order(4)
    void findByIdWithWrongOrigin() {
        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_TEST)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("id", book.getId())
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
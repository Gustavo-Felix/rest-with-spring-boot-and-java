package com.gustavofelix.rest_spring_boot.IntegrationTests.controllers.withxml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gustavofelix.rest_spring_boot.IntegrationTests.dto.BookDTO;
import com.gustavofelix.rest_spring_boot.IntegrationTests.dto.wrappers.xml.PagedModelBook;
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

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;

    private static BookDTO book;

    @BeforeAll
    static void setUp() {
        objectMapper = new XmlMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
    void createTest() throws JsonProcessingException {
        mockBooks();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)
                .addHeader("Accept", MediaType.APPLICATION_XML_VALUE)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                    .body(objectMapper.writeValueAsString(book))
                .when()
                    .post()
                .then()
                    .statusCode(201)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                    .body()
                        .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertEquals("Test Author", createdBook.getAuthor());
        assertEquals(Instant.parse("2007-12-03T10:15:30.00Z"), createdBook.getLaunchDate());
        assertEquals(180.00, createdBook.getPrice());
        assertEquals("Book For Test", createdBook.getTitle());

    }

    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        book.setTitle("Book For Test EDIT");

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                    .pathParam("id", book.getId())
                    .body(objectMapper.writeValueAsString(book))
                .when()
                    .put("{id}")
                .then()
                    .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                    .body()
                        .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertEquals("Test Author", createdBook.getAuthor());
        assertEquals(Instant.parse("2007-12-03T10:15:30.00Z"), createdBook.getLaunchDate());
        assertEquals(180.00, createdBook.getPrice());
        assertEquals("Book For Test EDIT", createdBook.getTitle());

    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertEquals("Test Author", createdBook.getAuthor());
        assertEquals(Instant.parse("2007-12-03T10:15:30.00Z"), createdBook.getLaunchDate());
        assertEquals(180.00, createdBook.getPrice());
        assertEquals("Book For Test EDIT", createdBook.getTitle());
    }

    @Test
    @Order(4)
    void deleteTest() throws JsonProcessingException {

        given(specification)
                .pathParam("id", book.getId())
            .when()
                .delete("{id}")
            .then()
                .statusCode(204);

    }

    @Test
    @Order(5)
    void findAllTest() throws JsonProcessingException {

        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .queryParam("page", 1, "size", 12, "direction", "ASC")
                .when()
                    .get()
                .then()
                    .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                    .body()
                        .asString();

        PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);
        List<BookDTO> books = wrapper.getContent();
        BookDTO bookOne = books.getFirst();
        BookDTO bookTwo = books.get(1);
        book = books.getFirst();

        assertNotNull(bookOne.getId());
        assertTrue(bookOne.getId() > 0);

        assertEquals("Crockford", bookOne.getAuthor());
        assertEquals("JavaScript", bookOne.getTitle());
        assertEquals(Instant.parse("2017-11-07T15:09:01.674Z"), bookOne.getLaunchDate());
        assertEquals(67.00, bookOne.getPrice());

        assertNotNull(bookTwo.getId());
        assertTrue(bookTwo.getId() > 0);

        assertEquals("Susan Cain", bookTwo.getAuthor());
        assertEquals("O poder dos quietos", bookTwo.getTitle());
        assertEquals(Instant.parse("2017-11-07T15:09:01.674Z"), bookTwo.getLaunchDate());
        assertEquals(123.00, bookTwo.getPrice());
    }
}
package com.gustavofelix.rest_spring_boot.IntegrationTests.controllers.withyaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gustavofelix.rest_spring_boot.IntegrationTests.controllers.withyaml.mapper.YAMLMapper;
import com.gustavofelix.rest_spring_boot.IntegrationTests.dto.BookDTO;
import com.gustavofelix.rest_spring_boot.IntegrationTests.dto.wrappers.json.book.WrapperBookDTO;
import com.gustavofelix.rest_spring_boot.IntegrationTests.dto.wrappers.xml.PagedModelBook;
import com.gustavofelix.rest_spring_boot.IntegrationTests.testcontainers.AbstractIntegrationTest;
import com.gustavofelix.rest_spring_boot.config.TestConfigs;
import com.gustavofelix.rest_spring_boot.serialization.converter.YamlJackson2HttpMessageConverter;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;

    private static BookDTO book;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();

        RestAssured.config = RestAssured.config()
                .encoderConfig(
                        EncoderConfig.encoderConfig()
                                .encodeContentTypeAs("application/x-yaml", ContentType.TEXT)
                );

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
                .addHeader("Accept", "application/x-yaml")
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var createdBook = given().config(
                RestAssuredConfig.config()
                        .encoderConfig(
                                EncoderConfig.encoderConfig().
                                        encodeContentTypeAs(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML, ContentType.TEXT))
                    ).spec(specification)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                    .body(book, objectMapper)
                .when()
                    .post()
                .then()
                    .statusCode(201)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .extract()
                    .body()
                        .as(BookDTO.class, objectMapper);

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

        var createdBook = given(specification)
                .contentType("application/x-yaml")
                    .pathParam("id", book.getId())
                    .body(book, objectMapper)
                .when()
                    .put("{id}")
                .then()
                    .statusCode(200)
                .contentType("application/x-yaml")
                .extract()
                    .body()
                        .as(BookDTO.class, objectMapper);

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

        var createdBook = given(specification)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .extract()
                .body()
                .as(BookDTO.class, objectMapper);

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
                .accept(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .queryParam("page", 1, "size", 12, "direction", "ASC")
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML)
                .extract()
                .body()
                .as(PagedModelBook.class, objectMapper);

        List<BookDTO> books = content.getContent();
        BookDTO bookOne = books.getFirst();
        BookDTO bookTwo = books.get(1);
        book = books.getFirst();

        assertNotNull(bookOne.getId());
        assertTrue(bookOne.getId() > 0);

        assertEquals("Richard Hunter e George Westerman", bookOne.getAuthor());
        assertEquals("O verdadeiro valor de TI", bookOne.getTitle());
        assertEquals(Instant.parse("2017-11-07T15:09:01.674Z"), bookOne.getLaunchDate());
        assertEquals(95.0, bookOne.getPrice());

        assertNotNull(bookTwo.getId());
        assertTrue(bookTwo.getId() > 0);

        assertEquals("Marc J. Schiller", bookTwo.getAuthor());
        assertEquals("Os 11 segredos de líderes de TI altamente influentes", bookTwo.getTitle());
        assertEquals(Instant.parse("2017-11-07T15:09:01.674Z"), bookTwo.getLaunchDate());
        assertEquals(45.0, bookTwo.getPrice());


    }
}
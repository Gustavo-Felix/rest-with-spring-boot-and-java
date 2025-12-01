package com.gustavofelix.rest_spring_boot.UnitTests.mapper;

import com.gustavofelix.rest_spring_boot.UnitTests.mapper.mocks.MockBook;
import com.gustavofelix.rest_spring_boot.dto.BookDTO;
import com.gustavofelix.rest_spring_boot.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static com.gustavofelix.rest_spring_boot.mapper.ObjectMapper.parseListObjects;
import static com.gustavofelix.rest_spring_boot.mapper.ObjectMapper.parseObject;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookMapperTests {
    MockBook inputObject;

    @BeforeEach
    public void setUp() {
        inputObject = new MockBook();
    }

    @Test
    public void parseEntityToDTOTest() {
        BookDTO output = parseObject(inputObject.mockEntity(), BookDTO.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Author Test0", output.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), output.getLaunchDate());
        assertEquals("Title Test0", output.getTitle());
    }

    @Test
    public void parseEntityListToDTOListTest() {
        List<BookDTO> outputList = parseListObjects(inputObject.mockEntityList(), BookDTO.class);
        BookDTO outputZero = outputList.get(0);

        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Author Test0", outputZero.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), outputZero.getLaunchDate());
        assertEquals("Title Test0", outputZero.getTitle());

        BookDTO outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Author Test7", outputSeven.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), outputSeven.getLaunchDate());
        assertEquals("Title Test7", outputSeven.getTitle());

        BookDTO outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Author Test12", outputTwelve.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), outputTwelve.getLaunchDate());
        assertEquals("Title Test12", outputTwelve.getTitle());
    }

    @Test
    public void parseDTOToEntityTest() {
        Book output = parseObject(inputObject.mockDTO(), Book.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Author Test0", output.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), output.getLaunchDate());
        assertEquals("Title Test0", output.getTitle());
    }

    @Test
    public void parserDTOListToEntityListTest() {
        List<Book> outputList = parseListObjects(inputObject.mockDTOList(), Book.class);
        Book outputZero = outputList.get(0);

        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Author Test0", outputZero.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), outputZero.getLaunchDate());
        assertEquals("Title Test0", outputZero.getTitle());

        Book outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Author Test7", outputSeven.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), outputSeven.getLaunchDate());
        assertEquals("Title Test7", outputSeven.getTitle());

        Book outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Author Test12", outputTwelve.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), outputTwelve.getLaunchDate());
        assertEquals("Title Test12", outputTwelve.getTitle());
    }

}

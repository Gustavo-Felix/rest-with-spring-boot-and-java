package com.gustavofelix.rest_spring_boot.UnitTests.mapper.mocks;

import com.gustavofelix.rest_spring_boot.dto.BookDTO;
import com.gustavofelix.rest_spring_boot.model.Book;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MockBook {
    public Book mockEntity() {
        return mockEntity(0);
    }

    public BookDTO mockDTO() {
        return mockDTO(0);
    }

    public List<Book> mockEntityList() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            books.add(mockEntity(i));
        }
        return books;
    }

    public List<BookDTO> mockDTOList() {
        List<BookDTO> books = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            books.add(mockDTO(i));
        }
        return books;
    }

    public Book mockEntity(Integer number) {
        Book book = new Book();
        book.setId(number.longValue());
        book.setAuthor("Author Test" + number);
        book.setLaunchDate(Instant.parse("2025-11-28T20:31:10Z"));
        book.setPrice(number.doubleValue());
        book.setTitle("Title Test" + number);
        return book;
    }

    public BookDTO mockDTO(Integer number) {
        BookDTO book = new BookDTO();
        book.setId(number.longValue());
        book.setAuthor("Author Test" + number);
        book.setLaunchDate(Instant.parse("2025-11-28T20:31:10Z"));
        book.setPrice(number.doubleValue());
        book.setTitle("Title Test" + number);
        return book;
    }
}

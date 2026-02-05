package com.gustavofelix.rest_spring_boot.UnitTests.service;

import com.gustavofelix.rest_spring_boot.UnitTests.mapper.mocks.MockBook;
import com.gustavofelix.rest_spring_boot.dto.BookDTO;
import com.gustavofelix.rest_spring_boot.exception.ResourceBadRequestException;
import com.gustavofelix.rest_spring_boot.model.Book;
import com.gustavofelix.rest_spring_boot.repository.BookRepository;
import com.gustavofelix.rest_spring_boot.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    MockBook input;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        List<Book> list = input.mockEntityList();
        when(bookRepository.findAll()).thenReturn(list);
        List<BookDTO> books = bookService.findAll();

        assertNotNull(books);
        assertEquals(14, books.size());

        var bookSeven = books.get(7);

        assertNotNull(bookSeven);
        assertNotNull(bookSeven.getId());
        assertNotNull(bookSeven.getLinks());

        bookSeven.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/book/7")
                        && link.getType().equals("GET"));

        bookSeven.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET"));

        bookSeven.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("insert")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST"));

        bookSeven.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/book/7")
                        && link.getType().equals("PUT"));

        bookSeven.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/book/7")
                        && link.getType().equals("DELETE"));

        assertEquals("Author Test7", bookSeven.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), bookSeven.getLaunchDate());
        assertEquals("Title Test7", bookSeven.getTitle());
    }

    @Test
    void findById() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        var result = bookService.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("insert")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("PUT"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("DELETE"));

        assertEquals("Author Test1", book.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), book.getLaunchDate());
        assertEquals("Title Test1", book.getTitle());
    }

    @Test
    void testInsertionWithNullBook() {
        Exception exception = assertThrows(ResourceBadRequestException.class,
                () -> {
                    bookService.insert(null);
                }
        );

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void insert() {
        Book entity = input.mockEntity(1);
        BookDTO dto = input.mockDTO(1);

        when(bookRepository.save(any(Book.class))).thenReturn(entity);
        var result = bookService.insert(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("insert")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("PUT"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("DELETE"));

        assertEquals("Author Test1", entity.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), entity.getLaunchDate());
        assertEquals("Title Test1", entity.getTitle());
    }

    @Test
    void testUpdateWithNullBook() {
        Exception exception = assertThrows(ResourceBadRequestException.class,
                () -> {
                    bookService.insert(null);
                }
        );

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() {
        Book book = input.mockEntity(1);
        Book persisted = book;
        persisted.setId(1L);

        BookDTO dto = input.mockDTO(1);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(persisted);
        var bookToUpdate = bookService.findById(1L);
        var result = bookService.update(bookToUpdate.getId(), dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("insert")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("PUT"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/book/1")
                        && link.getType().equals("DELETE"));

        assertEquals("Author Test1", book.getAuthor());
        assertEquals(Instant.parse("2025-11-28T20:31:10Z"), book.getLaunchDate());
        assertEquals("Title Test1", book.getTitle());
    }

    @Test
    void delete() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.delete(1L);

        verify(bookRepository, times(1)).findById(anyLong());
        verify(bookRepository, times(1)).delete(any(Book.class));
        verifyNoMoreInteractions(bookRepository);
    }
}
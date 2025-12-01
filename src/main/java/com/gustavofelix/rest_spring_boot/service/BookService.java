package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.controllers.BookController;
import com.gustavofelix.rest_spring_boot.dto.BookDTO;
import com.gustavofelix.rest_spring_boot.exception.ResourceBadRequestException;
import com.gustavofelix.rest_spring_boot.exception.ResourceNotFoundException;
import com.gustavofelix.rest_spring_boot.model.Book;
import com.gustavofelix.rest_spring_boot.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.gustavofelix.rest_spring_boot.mapper.ObjectMapper.parseListObjects;
import static com.gustavofelix.rest_spring_boot.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;

    private Logger logger = LoggerFactory.getLogger(BookService.class.getName());

    public List<BookDTO> findAll() {
        logger.info("Finding Books!");

        var dtos = parseListObjects(bookRepository.findAll(), BookDTO.class);
        dtos.forEach(BookService::addHateoasLinks);
        return dtos;
    }

    public BookDTO findById(Long id) {
        logger.info("Finding one Book!");

        var entity = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found!"));

        var dto = parseObject(entity, BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public BookDTO insert(BookDTO person) {

        if (person == null) throw new ResourceBadRequestException();

        logger.info("Creating a Book!");
        var entity = parseObject(person, Book.class);

        var dto = parseObject(bookRepository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public BookDTO update(Long id, BookDTO person) {
        logger.info("Updating a Book!");

        Book entity = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        updateEntity(entity, person);

        var dto = parseObject(bookRepository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting a Book!");

        Book person = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found!"));

        bookRepository.delete(person);
    }

    private void updateEntity(Book entity, BookDTO person) {
        entity.setAuthor(person.getAuthor());
        entity.setLaunchDate(person.getLaunchDate());
        entity.setPrice(person.getPrice());
        entity.setTitle(person.getTitle());
    }

    private static void addHateoasLinks(BookDTO dto) {
        dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).insert(dto)).withRel("insert").withType("POST"));
        dto.add(linkTo(methodOn(BookController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}

package com.gustavofelix.rest_spring_boot.controllers;

import com.gustavofelix.rest_spring_boot.controllers.docs.BookControllerDocs;
import com.gustavofelix.rest_spring_boot.dto.BookDTO;
import com.gustavofelix.rest_spring_boot.serialization.converter.YamlJackson2HttpMessageConverter;
import com.gustavofelix.rest_spring_boot.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "api/book/v1")
public class BookController implements BookControllerDocs {
    
    @Autowired
    private BookService bookService;

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML}
    )
    @Override
    public ResponseEntity<List<BookDTO>> findAll() {
        List<BookDTO> persons = bookService.findAll();

        return ResponseEntity.ok().body(persons);
    }

    @GetMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML}
    )
    @Override
    public ResponseEntity<BookDTO> findById(@PathVariable Long id) {
        BookDTO person = bookService.findById(id);
        return ResponseEntity.ok().body(person);
    }

    @PostMapping(consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML}
    )
    @Override
    public ResponseEntity<BookDTO> insert(@RequestBody BookDTO person) {
        BookDTO createdBookDTO = bookService.insert(person);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBookDTO.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/{id}", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML}
    )
    @Override
    public ResponseEntity<BookDTO> update(@PathVariable Long id, @RequestBody BookDTO person) {
        bookService.update(id, person);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

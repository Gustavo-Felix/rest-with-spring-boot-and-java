package com.gustavofelix.rest_spring_boot.controllers;

import com.gustavofelix.rest_spring_boot.controllers.docs.BookControllerDocs;
import com.gustavofelix.rest_spring_boot.dto.BookDTO;
import com.gustavofelix.rest_spring_boot.serialization.converter.YamlJackson2HttpMessageConverter;
import com.gustavofelix.rest_spring_boot.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
    public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "title"));

        return ResponseEntity.ok().body(bookService.findAll(pageable));
    }

    @GetMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML}
    )
    @Override
    public ResponseEntity<BookDTO> findById(@PathVariable Long id) {
        BookDTO book = bookService.findById(id);
        return ResponseEntity.ok().body(book);
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
    public ResponseEntity<BookDTO> insert(@RequestBody BookDTO book) {
        BookDTO createdBookDTO = bookService.insert(book);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBookDTO.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdBookDTO);
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
    public ResponseEntity<BookDTO> update(@PathVariable Long id, @RequestBody BookDTO book) {
        BookDTO bookDTO = bookService.update(id, book);
        return ResponseEntity.ok().body(bookDTO);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

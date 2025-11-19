package com.gustavofelix.rest_spring_boot.controllers;

import com.gustavofelix.rest_spring_boot.dto.PersonDTO;
import com.gustavofelix.rest_spring_boot.serialization.converter.YamlJackson2HttpMessageConverter;
import com.gustavofelix.rest_spring_boot.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML}
    )
    public ResponseEntity<List<PersonDTO>> findAll() {
        List<PersonDTO> persons = personService.findAll();

        return ResponseEntity.ok().body(persons);
    }

    @GetMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML}
    )
    public ResponseEntity<PersonDTO> findById(@PathVariable Long id) {
        PersonDTO person = personService.findById(id);
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
    public ResponseEntity<PersonDTO> insert(@RequestBody PersonDTO person) {
        PersonDTO createdPersonDTO = personService.insert(person);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPersonDTO.getId())
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
    public ResponseEntity<PersonDTO> update(@PathVariable Long id, @RequestBody PersonDTO person) {
        personService.update(id, person);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

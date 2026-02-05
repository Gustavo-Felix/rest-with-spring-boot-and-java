package com.gustavofelix.rest_spring_boot.controllers;

import com.gustavofelix.rest_spring_boot.controllers.docs.PersonControllerDocs;
import com.gustavofelix.rest_spring_boot.dto.PersonDTO;
import com.gustavofelix.rest_spring_boot.serialization.converter.YamlJackson2HttpMessageConverter;
import com.gustavofelix.rest_spring_boot.service.PersonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping(value = "api/person/v1")
@Tag(name = "People", description = "EndPoints for managing people!")
public class PersonController implements PersonControllerDocs {

    @Autowired
    private PersonService personService;

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML}
    )
    @Override
    public ResponseEntity<List<PersonDTO>> findAll() {
        List<PersonDTO> persons = personService.findAll();

        return ResponseEntity.ok().body(persons);
    }

    // @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML}
    )
    @Override
    public ResponseEntity<PersonDTO> findById(@PathVariable Long id) {
        PersonDTO person = personService.findById(id);
        return ResponseEntity.ok().body(person);
    }

    // @CrossOrigin(origins = {"http://localhost:8080", "http://otherhost.com"})
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
    public ResponseEntity<PersonDTO> insert(@RequestBody PersonDTO person) {
        PersonDTO createdPersonDTO = personService.insert(person);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPersonDTO.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdPersonDTO);
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
    public ResponseEntity<PersonDTO> update(@PathVariable Long id, @RequestBody PersonDTO person) {
        var updatedPerson = personService.update(id, person);
        return ResponseEntity.ok().body(updatedPerson);
    }

    @PatchMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            YamlJackson2HttpMessageConverter.MEDIA_TYPE_YAML}
    )
    @Override
    public ResponseEntity<PersonDTO> disablePerson(Long id) {
        var person = personService.disablePerson(id);
        return ResponseEntity.ok().body(person);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

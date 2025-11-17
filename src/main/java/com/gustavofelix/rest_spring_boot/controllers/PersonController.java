package com.gustavofelix.rest_spring_boot.controllers;

import com.gustavofelix.rest_spring_boot.dto.v1.PersonDTOV1;
import com.gustavofelix.rest_spring_boot.dto.v2.PersonDTOV2;
import com.gustavofelix.rest_spring_boot.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PersonDTOV1>> findAll() {
        List<PersonDTOV1> persons = personService.findAll();

        return ResponseEntity.ok().body(persons);
    }

    @GetMapping(value = "/v1/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTOV1> findById(@PathVariable Long id) {
        PersonDTOV1 person = personService.findById(id);

        return ResponseEntity.ok().body(person);
    }

    @PostMapping(value = "/v1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTOV1> insert(@RequestBody PersonDTOV1 person) {
        PersonDTOV1 createdPersonDTOV1 = personService.insert(person);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPersonDTOV1.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PostMapping(value = "/v2", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTOV2> insert(@RequestBody PersonDTOV2 person) {
        PersonDTOV2 createdPersonDTO = personService.insertV2(person);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPersonDTO.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/v1/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTOV1> update(@PathVariable Long id, @RequestBody PersonDTOV1 person) {
        personService.update(id, person);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/v1/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

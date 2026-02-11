package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.controllers.PersonController;
import com.gustavofelix.rest_spring_boot.dto.PersonDTO;
import com.gustavofelix.rest_spring_boot.exception.ResourceBadRequestException;
import com.gustavofelix.rest_spring_boot.exception.ResourceNotFoundException;
import com.gustavofelix.rest_spring_boot.model.Person;
import com.gustavofelix.rest_spring_boot.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import static com.gustavofelix.rest_spring_boot.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    PagedResourcesAssembler<PersonDTO> assembler;

    private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info("Finding Persons!");

        var people = personRepository.findAll(pageable);

        var peopleWithLinks = people.map(person -> {
            var dto = parseObject(person, PersonDTO.class);
            addHateoasLinks(dto);
            return dto;
        });

        Link findAllLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(PersonController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                String.valueOf(pageable.getSort()))
        ).withSelfRel();

        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Pageable pageable) {
        logger.info("Finding People by name!");

        var people = personRepository.findPeopleByName(firstName, pageable);

        var peopleWithLinks = people.map(person -> {
            var dto = parseObject(person, PersonDTO.class);
            addHateoasLinks(dto);
            return dto;
        });

        Link findAllLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(PersonController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                String.valueOf(pageable.getSort()))
        ).withSelfRel();

        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");

        var entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found!"));

        var dto = parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public PersonDTO insert(PersonDTO person) {

        if (person == null) throw new ResourceBadRequestException();

        logger.info("Creating a Person!");
        var entity = parseObject(person, Person.class);

        var dto = parseObject(personRepository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public PersonDTO update(Long id, PersonDTO person) {
        logger.info("Updating a Person!");

        Person entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        updateEntity(entity, person);

        var dto = parseObject(personRepository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public PersonDTO disablePerson(Long id) {
        logger.info("Disabling a Person!");

        personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found!"));

        personRepository.disablePerson(id);

        var entity = personRepository.findById(id).get();
        var dto = parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);

        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting a Person!");

        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found!"));

        personRepository.delete(person);
    }

    private void updateEntity(Person entity, PersonDTO person) {
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
    }

    private static void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).insert(dto)).withRel("insert").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).disablePerson(dto.getId())).withRel("disable").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}

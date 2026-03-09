package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.controllers.PersonController;
import com.gustavofelix.rest_spring_boot.dto.PersonDTO;
import com.gustavofelix.rest_spring_boot.exception.BadRequestException;
import com.gustavofelix.rest_spring_boot.exception.FileStorageException;
import com.gustavofelix.rest_spring_boot.exception.ResourceBadRequestException;
import com.gustavofelix.rest_spring_boot.exception.ResourceNotFoundException;
import com.gustavofelix.rest_spring_boot.file.exporter.contract.PersonExporter;
import com.gustavofelix.rest_spring_boot.file.exporter.factory.FileExporterFactory;
import com.gustavofelix.rest_spring_boot.file.importer.contract.FileImporter;
import com.gustavofelix.rest_spring_boot.file.importer.factory.FileImporterFactory;
import com.gustavofelix.rest_spring_boot.model.Person;
import com.gustavofelix.rest_spring_boot.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static com.gustavofelix.rest_spring_boot.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private FileImporterFactory importer;

    @Autowired
    private FileExporterFactory exporter;

    @Autowired
    PagedResourcesAssembler<PersonDTO> assembler;

    private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info("Finding Persons!");

        var people = personRepository.findAll(pageable);

        return buildPagedModel(pageable, people);
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Pageable pageable) {
        logger.info("Finding People by name!");

        var people = personRepository.findPeopleByName(firstName, pageable);

        return buildPagedModel(pageable, people);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");

        var entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found!"));

        var dto = parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {

        logger.info("Exporting a People page!");

        var people = personRepository.findAll(pageable)
                .map(person -> parseObject(person, PersonDTO.class))
                .getContent();

        try {
            PersonExporter exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportPeople(people);
        } catch (Exception e) {
            throw new RuntimeException("Error during file export!", e);
        }
    }

    public Resource exportPerson(Long id, String acceptHeader) {
        logger.info("Exporting data of one Person!");

        var person = personRepository.findById(id)
                .map(entity -> parseObject(entity, PersonDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        try {
            PersonExporter exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportPerson(person);
        } catch (Exception e) {
            throw new RuntimeException("Error during file export!", e);
        }
    }

    public PersonDTO insert(PersonDTO person) {

        if (person == null) throw new ResourceBadRequestException();

        logger.info("Creating a Person!");
        var entity = parseObject(person, Person.class);

        var dto = parseObject(personRepository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public List<PersonDTO> massCreation(MultipartFile file) {
        logger.info("Importing people from file!");

        if (file.isEmpty()) throw new BadRequestException("Please, set a Valid File");

        try (InputStream inputStream = file.getInputStream()){
            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File name cannot be null!"));

            FileImporter importer = this.importer.getImporter(fileName);

            List<Person> entities = importer.importFile(inputStream).stream()
                    .map(dto -> personRepository.save(parseObject(dto, Person.class)))
                    .toList();

            return entities.stream()
                    .map(entity -> {
                        var dto = parseObject(entity, PersonDTO.class);
                        addHateoasLinks(dto);
                        return dto;
                    })
                    .toList();

        } catch (Exception e) {
            throw new FileStorageException("Error processing the File!");
        }
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

    private PagedModel<EntityModel<PersonDTO>> buildPagedModel(Pageable pageable, Page<Person> people) {
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

    private static void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findByName("", 1, 12, "asc")).withRel("findByName").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).insert(dto)).withRel("insert").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto.getId(), dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).disablePerson(dto.getId())).withRel("disable").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(PersonController.class).exportPage(
                1, 12, "asc", null))
                .withRel("exportPage")
                .withType("GET")
                .withTitle("Export People")
        );
        dto.add(linkTo(methodOn(PersonController.class).export(
                dto.getId(), null))
                .withRel("exportPerson")
                .withType("GET")
                .withTitle("Export Person")
        );
    }
}

package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.dto.v1.PersonDTOV1;
import com.gustavofelix.rest_spring_boot.dto.v2.PersonDTOV2;
import com.gustavofelix.rest_spring_boot.exception.ResourceNotFoundException;
import com.gustavofelix.rest_spring_boot.mapper.custom.PersonMapper;
import com.gustavofelix.rest_spring_boot.model.Person;
import com.gustavofelix.rest_spring_boot.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.gustavofelix.rest_spring_boot.mapper.ObjectMapper.parseListObjects;
import static com.gustavofelix.rest_spring_boot.mapper.ObjectMapper.parseObject;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonMapper converter;

    private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

    public List<PersonDTOV1> findAll() {
        logger.info("Finding Persons!");

        return parseListObjects(personRepository.findAll(), PersonDTOV1.class);
    }

    public PersonDTOV1 findById(Long id) {
        logger.info("Finding one Person!");

        var entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found!"));

        return parseObject(entity, PersonDTOV1.class);
    }

    public PersonDTOV1 insert(PersonDTOV1 person) {
        logger.info("Creating a Person!");
        var entity = parseObject(person, Person.class);

        return parseObject(personRepository.save(entity), PersonDTOV1.class);
    }

    public PersonDTOV2 insertV2(PersonDTOV2 person) {
        logger.info("Creating a Person V2!");
        var entity = converter.convertDTOtoEntity(person);

        return converter.convertEntityToDTO(personRepository.save(entity));
    }

    public PersonDTOV1 update(Long id, PersonDTOV1 person) {
        logger.info("Updating a Person!");

        Person entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        updateEntity(entity, person);

        return parseObject(personRepository.save(entity), PersonDTOV1.class);
    }

    public void delete(Long id) {
        logger.info("Deleting a Person!");

        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found!"));

        personRepository.delete(person);
    }

    private void updateEntity(Person entity, PersonDTOV1 person) {
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
    }

}

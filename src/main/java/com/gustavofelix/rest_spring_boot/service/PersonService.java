package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.exception.ResourceNotFoundException;
import com.gustavofelix.rest_spring_boot.model.Person;
import com.gustavofelix.rest_spring_boot.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public List<Person> findAll() {
        logger.info("Finding Persons!");

        return personRepository.findAll();
    }

    public Person findById(Long id) {
        logger.info("Finding one Person!");

        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found!"));
    }

    public Person insert(Person person) {
        logger.info("Creating a Person!");
        return personRepository.save(person);
    }

    public Person update(Long id, Person person) {
        logger.info("Updating a Person!");

        Person entity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        updateEntity(entity, person);

        return personRepository.save(entity);
    }

    public void delete(Long id) {
        logger.info("Deleting a Person!");

        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found!"));

        personRepository.delete(person);
    }

    private void updateEntity(Person entity, Person person) {
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
    }

}

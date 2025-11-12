package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {

    private final AtomicLong counter = new AtomicLong();

    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public List<Person> findAll() {
        logger.info("Finding Persons!");
        List<Person> persons = new ArrayList<>();

        for (int i = 0; i < 8; i++){
            Person person = mockPerson(i);
            persons.add(person);
        }

        return persons;
    }

    public Person findById(String id) {
        logger.info("Finding one Person!");

        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Gustavo");
        person.setLastName("Camargo");
        person.setAddress("SP");
        person.setGender("Male");

        return person;
    }

    public Person create(Person person) {
        logger.info("Creating a Person!");

        return person;
    }

    public Person update(Person person) {
        logger.info("Updating a Person!");

        return person;
    }

    public void delete(String id) {
        logger.info("Deleting a Person!");
    }

    private Person mockPerson(int i) {
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Gustavo" + i+1);
        person.setLastName("Camargo");
        person.setAddress("SP");
        person.setGender("Male");
        return person;
    }

}

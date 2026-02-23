package com.gustavofelix.rest_spring_boot.repository;

import com.gustavofelix.rest_spring_boot.IntegrationTests.testcontainers.AbstractIntegrationTest;
import com.gustavofelix.rest_spring_boot.model.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private PersonRepository personRepository;

    private static Person person;

    @BeforeAll
    static void setUp() {
        person = new Person();

    }

    @Test
    @Order(1)
    void disablePerson() {
        personRepository.disablePerson(1L);

        person = personRepository.findById(1L).orElseThrow(AssertionError::new);

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Gustavo", person.getFirstName());
        assertEquals("Camargo", person.getLastName());
        assertEquals("SP", person.getAddress());
        assertEquals("Male", person.getGender());
        assertFalse(person.getEnabled());
    }

    @Test
    @Order(2)
    void findPeopleByName() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.ASC, "firstName"));

        person = personRepository.findPeopleByName("run", pageable).getContent().getFirst();

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Bruna", person.getFirstName());
        assertEquals("Camargo", person.getLastName());
        assertEquals("SP", person.getAddress());
        assertEquals("Female", person.getGender());
        assertTrue(person.getEnabled());
    }
}
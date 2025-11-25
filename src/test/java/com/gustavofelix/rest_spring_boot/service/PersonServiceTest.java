package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.UnitTests.mapper.mocks.MockPerson;
import com.gustavofelix.rest_spring_boot.dto.PersonDTO;
import com.gustavofelix.rest_spring_boot.exception.ResourceBadRequestException;
import com.gustavofelix.rest_spring_boot.exception.ResourceNotFoundException;
import com.gustavofelix.rest_spring_boot.model.Person;
import com.gustavofelix.rest_spring_boot.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.gustavofelix.rest_spring_boot.mapper.ObjectMapper.parseObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    MockPerson input;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    void setUp() {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        List<Person> list = input.mockEntityList();
        when(personRepository.findAll()).thenReturn(list);
        List<PersonDTO> people = personService.findAll();
        
        assertNotNull(people);
        assertEquals(14, people.size());
        
        var personSeven = people.get(7);

        assertNotNull(personSeven);
        assertNotNull(personSeven.getId());
        assertNotNull(personSeven.getLinks());

        personSeven.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/7")
                        && link.getType().equals("GET"));

        personSeven.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET"));

        personSeven.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("insert")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("POST"));

        personSeven.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person/7")
                        && link.getType().equals("PUT"));

        personSeven.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/7")
                        && link.getType().equals("DELETE"));

        assertEquals("Address Test7", personSeven.getAddress());
        assertEquals("First Name Test7", personSeven.getFirstName());
        assertEquals("Last Name Test7", personSeven.getLastName());
        assertEquals("Female", personSeven.getGender());
    }

    @Test
    void findById() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        var result = personService.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("insert")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("POST"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("PUT"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("DELETE"));

        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void testInsertionWithNullPerson() {
        Exception exception = assertThrows(ResourceBadRequestException.class,
                () -> {
                    personService.insert(null);
                }
        );

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void insert() {
        Person person = input.mockEntity(1);
        Person persisted = person;
        persisted.setId(1L);

        PersonDTO dto = input.mockDTO(1);

        when(personRepository.save(person)).thenReturn(persisted);
        var result = personService.insert(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("insert")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("POST"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("PUT"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("DELETE"));

        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void testUpdateWithNullPerson() {
        Exception exception = assertThrows(ResourceBadRequestException.class,
                () -> {
                    personService.insert(null);
                }
        );

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() {
        Person person = input.mockEntity(1);
        Person persisted = person;
        persisted.setId(1L);

        PersonDTO dto = input.mockDTO(1);

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.save(person)).thenReturn(persisted);
        var personToUpdate = personService.findById(1L);
        var result = personService.update(personToUpdate.getId(), dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("GET"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("insert")
                        && link.getHref().endsWith("/api/person/v1")
                        && link.getType().equals("POST"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("PUT"));

        result.getLinks()
                .stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && link.getType().equals("DELETE"));

        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void delete() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        personService.delete(1L);

        verify(personRepository, times(1)).findById(anyLong());
        verify(personRepository, times(1)).delete(any(Person.class));
        verifyNoMoreInteractions(personRepository);
    }
}
package com.gustavofelix.rest_spring_boot.mapper.custom;

import com.gustavofelix.rest_spring_boot.dto.v2.PersonDTOV2;
import com.gustavofelix.rest_spring_boot.model.Person;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PersonMapper {

    public PersonDTOV2 convertEntityToDTO(Person person) {
        PersonDTOV2 dto = new PersonDTOV2();

        dto.setId(person.getId());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setBirthDay(new Date());
        dto.setAddress(person.getAddress());
        dto.setGender(person.getGender());

        return dto;
    }

    public Person convertDTOtoEntity(PersonDTOV2 personDTO) {
        Person person = new Person();

        person.setFirstName(personDTO.getFirstName());
        person.setLastName(personDTO.getLastName());
        //person.setBirthDay(new Date());
        person.setAddress(personDTO.getAddress());
        person.setGender(personDTO.getGender());

        return person;
    }

}

package com.gustavofelix.rest_spring_boot.file.exporter.contract;

import com.gustavofelix.rest_spring_boot.dto.PersonDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface PersonExporter {
    Resource exportPeople(List<PersonDTO> people) throws Exception;
    Resource exportPerson(PersonDTO person) throws Exception;
}


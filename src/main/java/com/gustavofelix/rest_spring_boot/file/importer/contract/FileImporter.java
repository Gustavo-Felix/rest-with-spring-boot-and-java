package com.gustavofelix.rest_spring_boot.file.importer.contract;

import com.gustavofelix.rest_spring_boot.dto.PersonDTO;

import java.io.InputStream;
import java.util.List;

public interface FileImporter {

    List<PersonDTO> importFile(InputStream inputStream) throws Exception;
}

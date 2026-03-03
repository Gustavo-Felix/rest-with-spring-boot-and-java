package com.gustavofelix.rest_spring_boot.file.exporter.contract;

import com.gustavofelix.rest_spring_boot.dto.PersonDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileExporter {

    Resource exportFile(List<PersonDTO> people) throws Exception;
}

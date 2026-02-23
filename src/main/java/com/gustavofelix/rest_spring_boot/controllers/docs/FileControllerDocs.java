package com.gustavofelix.rest_spring_boot.controllers.docs;

import com.gustavofelix.rest_spring_boot.dto.UploadFileResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File EndPoint")
public interface FileControllerDocs {

    UploadFileResponseDTO uploadFile(MultipartFile file);
    List<UploadFileResponseDTO> uploadMultipleFile(MultipartFile[] files);
    ResponseEntity<ResponseEntity> downloadFile(String fileName, HttpServletRequest request);


}

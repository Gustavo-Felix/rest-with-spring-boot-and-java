package com.gustavofelix.rest_spring_boot.controllers;

import com.gustavofelix.rest_spring_boot.controllers.docs.EmailControllerDocs;
import com.gustavofelix.rest_spring_boot.dto.request.EmailRequestDTO;
import com.gustavofelix.rest_spring_boot.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/email/v1")
public class EmailController implements EmailControllerDocs {

    @Autowired
    private EmailService emailService;

    @Override
    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
        emailService.sendSimpleEmail(emailRequestDTO);
        return new ResponseEntity<>("E-mail send successfully!", HttpStatus.OK);
    }

    @Override
    @PostMapping(value = "/withAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("emailRequest") String emailRequest,
            @RequestParam("attachment") MultipartFile attachment)
    {
        emailService.sendEmailWithAttachment(emailRequest, attachment);

        return new ResponseEntity<>("E-mail with attachment send successfully!", HttpStatus.OK);
    }
}

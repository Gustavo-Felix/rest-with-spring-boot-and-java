package com.gustavofelix.rest_spring_boot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavofelix.rest_spring_boot.config.EmailConfig;
import com.gustavofelix.rest_spring_boot.dto.request.EmailRequestDTO;
import com.gustavofelix.rest_spring_boot.mail.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private EmailConfig emailConfigs;

    public void sendSimpleEmail(EmailRequestDTO emailRequestDTO) {
        emailSender
            .to(emailRequestDTO.getTo())
            .withSubject(emailRequestDTO.getSubject())
            .withMessage(emailRequestDTO.getBody())
            .send(emailConfigs);
    }

    public void sendEmailWithAttachment(String emailRequestJson, MultipartFile attachment) {
        File tempFile = null;
        try {
            EmailRequestDTO emailRequest = new ObjectMapper().readValue(emailRequestJson, EmailRequestDTO.class);
            tempFile = File.createTempFile("attachment", attachment.getOriginalFilename());
            attachment.transferTo(tempFile);

            emailSender
                .to(emailRequest.getTo())
                .withSubject(emailRequest.getSubject())
                .withMessage(emailRequest.getBody())
                .withAttach(tempFile.getAbsolutePath())
                .send(emailConfigs);

        } catch (JsonProcessingException e ) {
            throw new RuntimeException("Failed to parse email request JSON!", e);
        } catch (IOException e) {
            throw new RuntimeException("Error processing the attachment!", e);
        } finally {
            if (tempFile != null && tempFile.exists()) tempFile.delete();
        }
    }

}

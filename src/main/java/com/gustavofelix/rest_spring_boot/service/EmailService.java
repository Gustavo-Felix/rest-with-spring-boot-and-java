package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.config.EmailConfig;
import com.gustavofelix.rest_spring_boot.dto.request.EmailRequestDTO;
import com.gustavofelix.rest_spring_boot.mail.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}

package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private JavaMailSender emailSender;

    @Value("${email.from}")
    private String emailFrom;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendEmail(String emailTo, String subject, String message) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            mimeMessageHelper.setTo(emailTo);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message, true);
            mimeMessageHelper.setFrom(emailFrom);
        } catch (MessagingException e) {
            log.error("Error while creating message to {}", emailTo);
            log.error("Error", e);
        }
        log.info("Sending message to: {}", emailTo);
        emailSender.send(mimeMessage);
        log.info("message to {} was sent", emailTo);
    }
}
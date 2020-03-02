package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.EmailConfirmationInfo;

public interface EmailService {
    void sendEmailConfirmationLetter(EmailConfirmationInfo emailConfirmationInfo);
    void sendEmail(String to, String subject, String message);
}
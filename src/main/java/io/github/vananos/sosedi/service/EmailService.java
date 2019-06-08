package io.github.vananos.sosedi.service;

public interface EmailService {
    void sendEmail(String to, String subject, String message);
}
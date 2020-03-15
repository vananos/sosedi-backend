package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.events.UserRegisteredEvent;
import io.github.vananos.sosedi.models.events.NewPinCodeRequestedEvent;

public interface EmailService {
    void sendEmailConfirmationLetter(UserRegisteredEvent userRegisteredEvent);

    void sendLetterWithNewPinCode(NewPinCodeRequestedEvent newPinCodeRequestedEvent);

    void sendEmail(String to, String subject, String message);
}
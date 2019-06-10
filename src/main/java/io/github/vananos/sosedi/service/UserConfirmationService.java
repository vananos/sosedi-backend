package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.User;

import java.util.Optional;

public interface UserConfirmationService {
    void sendConfirmationLetter(User user);

    Optional<User> confirmEmail(String confirmationId);

    String generateLink();
}
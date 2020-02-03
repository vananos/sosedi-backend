package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.User;

public interface PasswordResetService {

    void startPasswordResetForUser(User user);
}
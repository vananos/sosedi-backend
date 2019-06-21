package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.User;

public interface PasswordResetService {

    void resetPassword(String secret, String newPassword);

    void startPasswordResetForUser(User user);
}
package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.exceptions.UserAlreadyExists;
import io.github.vananos.sosedi.models.User;

public interface UserService {
    void registerUser(User user) throws UserAlreadyExists;
}

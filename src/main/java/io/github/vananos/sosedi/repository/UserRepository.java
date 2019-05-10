package io.github.vananos.sosedi.repository;

import io.github.vananos.sosedi.models.User;

public interface UserRepository {
    User getUserByEmail(String email);
}

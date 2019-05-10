package io.github.vananos.sosedi.repository.impl;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.UserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public User getUserByEmail(String email) {
        return null;
    }
}

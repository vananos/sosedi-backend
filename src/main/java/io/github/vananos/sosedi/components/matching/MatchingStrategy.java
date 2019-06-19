package io.github.vananos.sosedi.components.matching;

import io.github.vananos.sosedi.models.User;

public interface MatchingStrategy {
    boolean matches(User user, User secondUser);
}
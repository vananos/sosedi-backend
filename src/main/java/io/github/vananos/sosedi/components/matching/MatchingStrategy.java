package io.github.vananos.sosedi.components.matching;

import io.github.vananos.sosedi.models.User;

import java.util.function.Predicate;

public interface MatchingStrategy {
    Predicate<User> matchWithUser(User user);
}
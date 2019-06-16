package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.models.User;

import java.util.List;

public interface MatchService {

    List<Match> getMatchesForUser(Long userId);

    void updateMatchesForUser(User user);
}
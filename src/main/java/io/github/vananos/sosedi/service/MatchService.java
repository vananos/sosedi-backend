package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.matching.MatchUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface MatchService {

    List<Match> getMatchesForUser(Long userId);

    void updateMatchesForUser(User user);

    Optional<Match> getMatch(Long matchId);

    void updateMatchState(MatchUpdateRequest matchUpdateRequest);

    List<Match> getMutualMatches(Long userId);
}
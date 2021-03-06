package io.github.vananos.sosedi.repository;

import io.github.vananos.sosedi.models.Match;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("select m from Match m where (m.firstUser.id = :userId and m.firstUserState = 'NEW' ) or (m.secondUser.id " +
            "= " +
            ":userId) and m.secondUserState = 'NEW'")
    List<Match> findNewMatchesForUser(@Param("userId") Long userId, Pageable pageable);

    @Query("select m from Match m where m.firstUserState = 'NEW' or m.secondUserState = 'NEW'")
    List<Match> findNewMatches();

    @Query("select m from Match m where m.firstUserState = 'ACCEPTED' and m.secondUserState = 'ACCEPTED' and (m" +
            ".firstUser.id = :userId or m.secondUser.id = :userId)")
    List<Match> findMutualMatches(@Param("userId") Long userId);
}
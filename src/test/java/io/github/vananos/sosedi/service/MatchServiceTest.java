package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.components.matching.MatchingStrategy;
import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.models.Match.MatchState;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.matching.MatchUpdateRequest;
import io.github.vananos.sosedi.models.dto.matching.MatchUpdateRequest.MatchUpdateAction;
import io.github.vananos.sosedi.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MatchServiceTest {
    @MockBean
    private MatchRepository matchRepository;

    @MockBean
    private MatchingStrategy matchingStrategy;

    @Autowired
    private MatchService matchService;

    @Test
    public void getMatchFromRepository() {

        when(matchRepository.findNewMatchesForUser(any(), any()))
                .thenReturn(asList(new Match()));

        assertThat(matchService.getMatchesForUser(1L)).hasSize(1);

        verify(matchRepository, times(1)).findNewMatchesForUser(eq(1L), any());
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    public void updateMatchStateTest(MatchUpdateRequest matchUpdateRequest, Match expectedMatch) {
        when(matchRepository.getOne(any())).thenReturn(getValidMatch(MatchState.NEW, MatchState.NEW));

        matchService.updateMatchState(matchUpdateRequest);

        verify(matchRepository, times(1)).save(expectedMatch);
    }

    public static Stream<Arguments> provideArguments() {
        MatchUpdateRequest updateRequestForFirstUser = getValidMatchUpdateRequest(MatchUpdateAction.ACCEPT);
        Match firstUserUpdatedMatch = getValidMatch(MatchState.ACCEPTED, MatchState.NEW);

        MatchUpdateRequest updateRequestForSecondUser = getValidMatchUpdateRequest(MatchUpdateAction.DECLINE);
        updateRequestForSecondUser.setUserId(2L);
        Match secondUserUpdatedMatch = getValidMatch(MatchState.NEW, MatchState.DECLINED);

        return Stream.of(
                Arguments.of(updateRequestForFirstUser, firstUserUpdatedMatch),
                Arguments.of(updateRequestForSecondUser, secondUserUpdatedMatch)
        );
    }

    private static MatchUpdateRequest getValidMatchUpdateRequest(MatchUpdateAction action) {
        MatchUpdateRequest matchUpdateRequest = new MatchUpdateRequest();
        matchUpdateRequest.setUserId(1L);
        matchUpdateRequest.setMatchId(1L);
        matchUpdateRequest.setMatchUpdateAction(action);
        return matchUpdateRequest;
    }

    private static Match getValidMatch(MatchState firstState, MatchState secondState) {
        Match match = new Match();
        User firstUser = new User();
        firstUser.setId(1L);
        User secondUser = new User();
        secondUser.setId(2L);

        match.setFirstUser(firstUser);
        match.setSecondUser(secondUser);

        match.setFirstUserState(firstState);
        match.setSecondUserState(secondState);
        return match;
    }

}
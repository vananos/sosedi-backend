package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.components.matching.MatchingStrategy;
import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

        when(matchRepository.findNewMatchesForUser(any()))
                .thenReturn(asList(new Match()));

        assertThat(matchService.getMatchesForUser(1L)).hasSize(1);

        verify(matchRepository, times(1)).findNewMatchesForUser(eq(1L));
    }
}
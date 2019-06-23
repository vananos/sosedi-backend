package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.components.matching.MatchingStrategy;
import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.models.Match.MatchState;
import io.github.vananos.sosedi.models.MatchProcessorTask;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.User.UserStatus;
import io.github.vananos.sosedi.models.dto.matching.MatchUpdateRequest;
import io.github.vananos.sosedi.models.dto.matching.MatchUpdateRequest.MatchUpdateAction;
import io.github.vananos.sosedi.repository.MatchProcessorTaskRepository;
import io.github.vananos.sosedi.repository.MatchRepository;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.vananos.sosedi.models.Match.MatchState.ACCEPTED;
import static io.github.vananos.sosedi.models.Match.MatchState.DECLINED;
import static java.lang.String.format;

@Service
public class MatchServiceImpl implements MatchService {

    @Value("${matching.workers}")
    private int workersCount;

    private MatchRepository matchRepository;
    private MatchingStrategy matchingStrategy;
    private UserRepository userRepository;
    private MatchProcessorTaskRepository matchProcessorTaskRepository;

    private ExecutorService matchingFinderWorkers;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository, MatchingStrategy matchingStrategy,
                            MatchProcessorTaskRepository matchProcessorTaskRepository, UserRepository userRepository)
    {
        this.matchRepository = matchRepository;
        this.matchingStrategy = matchingStrategy;
        this.matchProcessorTaskRepository = matchProcessorTaskRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        matchingFinderWorkers = Executors.newFixedThreadPool(workersCount);
    }

    @Override
    public List<Match> getMatchesForUser(Long userId) {
        return matchRepository.findNewMatchesForUser(userId, PageRequest.of(0, 50));
    }

    @Override
    public void updateMatchesForUser(User user) {
        MatchProcessorTask matchProcessorTask = new MatchProcessorTask();
        matchProcessorTask.setUser(user);
        matchProcessorTask = matchProcessorTaskRepository.save(matchProcessorTask);

        submitTaskToMatchFinder(matchProcessorTask);
    }

    @Override
    public Optional<Match> getMatch(Long matchId) {
        return Optional.ofNullable(matchRepository.getOne(matchId));
    }

    @Override
    public void updateMatchState(MatchUpdateRequest matchUpdateRequest) {
        Match match = matchRepository.getOne(matchUpdateRequest.getMatchId());

        MatchState matchState = matchUpdateRequest.getMatchUpdateAction() == MatchUpdateAction.ACCEPT ? ACCEPTED :
                DECLINED;

        if (match.getFirstUser().getId().equals(matchUpdateRequest.getUserId())) {
            match.setFirstUserState(matchState);

        } else if (match.getSecondUser().getId().equals(matchUpdateRequest.getUserId())) {
            match.setSecondUserState(matchState);
        } else {
            throw new IllegalArgumentException(format("wrong userId %s for match %s", matchUpdateRequest.getUserId(),
                    match.getMatchId()
            ));
        }

        matchRepository.save(match);
    }

    private void submitTaskToMatchFinder(MatchProcessorTask matchProcessorTask) {

        matchingFinderWorkers.submit(() -> {

                    matchProcessorTask.setStatus(MatchProcessorTask.TaskStatus.PROCESSING);
                    matchProcessorTask.setStartProcessing(LocalDateTime.now());
                    matchProcessorTaskRepository.save(matchProcessorTask);

                    userRepository.findAll()
                            .stream()
                            .filter(secondUser -> secondUser.getUserStatus() == UserStatus.AD_FILLED)
                            .filter(secondUser -> matchingStrategy.matches(matchProcessorTask.getUser(), secondUser))
                            .map(user -> {
                                Match match = new Match();
                                match.setFirstUser(matchProcessorTask.getUser());
                                match.setSecondUser(user);
                                return match;
                            })
                            .forEach(match -> matchRepository.save(match));

                    matchProcessorTask.setEndProcessing(LocalDateTime.now());
                    matchProcessorTask.setStatus(MatchProcessorTask.TaskStatus.PROCESSED);
                    matchProcessorTaskRepository.save(matchProcessorTask);
                }
        );
    }
}
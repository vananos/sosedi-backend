package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.components.matching.MatchingStrategy;
import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.models.MatchProcessorTask;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.MatchProcessorTaskRepository;
import io.github.vananos.sosedi.repository.MatchRepository;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        return matchRepository.findNewMatchesForUser(userId);
    }

    @Override
    public void updateMatchesForUser(User user) {
        MatchProcessorTask matchProcessorTask = new MatchProcessorTask();
        matchProcessorTask.setUser(user);
        matchProcessorTask = matchProcessorTaskRepository.save(matchProcessorTask);

        submitTaskToMatchFinder(matchProcessorTask);
    }

    private void submitTaskToMatchFinder(MatchProcessorTask matchProcessorTask) {

        matchingFinderWorkers.submit(() -> {
                    matchProcessorTask.setStatus(MatchProcessorTask.TaskStatus.PROCESSING);
                    matchProcessorTask.setStartProcessing(LocalDateTime.now());
                    matchProcessorTaskRepository.save(matchProcessorTask);

                    userRepository.findAll()
                            .stream()
                            .filter(matchingStrategy.matchWithUser(matchProcessorTask.getUser()))
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
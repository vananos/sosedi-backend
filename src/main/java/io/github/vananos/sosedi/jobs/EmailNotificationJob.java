package io.github.vananos.sosedi.jobs;

import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.models.Match.MatchState;
import io.github.vananos.sosedi.models.NotificationFrequency;
import io.github.vananos.sosedi.models.Notifications;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.MatchRepository;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class EmailNotificationJob {

    public static final int HOUR_DELAY = 1000 * 3600;

    private EmailService emailService;
    private MatchRepository matchRepository;
    private TemplateEngine templateEngine;
    private UserRepository userRepository;

    @Autowired
    public EmailNotificationJob(EmailService emailService, MatchRepository matchRepository,
                                TemplateEngine templateEngine, UserRepository userRepository)
    {
        this.emailService = emailService;
        this.matchRepository = matchRepository;
        this.templateEngine = templateEngine;
        this.userRepository = userRepository;
    }


    @Scheduled(fixedDelay = HOUR_DELAY)
    public void notifyUsersAboutNewMatches() {
        log.info("starting notification job");

        List<Match> matchList = matchRepository.findNewMatches();

        Map<User, Integer> userMatchCountMap = new HashMap<>();

        for (Match match : matchList) {
            if (match.getFirstUserState() == MatchState.NEW) {
                userMatchCountMap.merge(match.getFirstUser(), 1, Integer::sum);
            }
            if (match.getSecondUserState() == MatchState.NEW) {
                userMatchCountMap.merge(match.getSecondUser(), 1, Integer::sum);
            }
        }

        userMatchCountMap.entrySet()
                .parallelStream()
                .forEach(entry -> {
                    User user = entry.getKey();

                    if (canSendNotificationNow(user)) {
                        sendNotification(user, entry.getValue());
                    }
                });

        userMatchCountMap.keySet().forEach(user -> {
            user.getNotifications().setLastSentTime(LocalDateTime.now());
        });

        userRepository.saveAll(userMatchCountMap.keySet());

        log.info("job done");
    }

    private void sendNotification(User user, Integer value) {
        Context context = new Context();
        context.setVariable("matchCount", value);
        context.setVariable("userName", user.getName());

        String letter = templateEngine.process("matchNotification", context);
        emailService.sendEmail(user.getEmail(), "Новые соседи", letter);
    }

    private boolean canSendNotificationNow(User user) {
        Notifications notifications = user.getNotifications();
        NotificationFrequency frequency = notifications.getNotificationFrequency();
        LocalDateTime lastSent = notifications.getLastSentTime();

        if (frequency != NotificationFrequency.NEVER && lastSent == null) {
            return true;
        }
        if (frequency == NotificationFrequency.ONE_DAY) {
            return lastSent.until(LocalDateTime.now(), ChronoUnit.DAYS) >= 1;
        }
        if (frequency == NotificationFrequency.ONE_HOUR) {
            return lastSent.until(LocalDateTime.now(), ChronoUnit.HOURS) >= 1;
        }

        return false;
    }
}

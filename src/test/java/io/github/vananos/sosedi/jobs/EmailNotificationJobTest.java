package io.github.vananos.sosedi.jobs;

import io.github.vananos.sosedi.models.Match;
import io.github.vananos.sosedi.models.Match.MatchState;
import io.github.vananos.sosedi.models.NotificationFrequency;
import io.github.vananos.sosedi.models.Notifications;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.MatchRepository;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.EmailService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmailNotificationJobTest {

    private static long userIdCounter = 0;

    @Autowired
    private EmailNotificationJob emailNotificationJob;

    @MockBean
    private EmailService emailService;

    @MockBean
    private MatchRepository matchRepository;

    @MockBean
    private UserRepository userRepository;

    @ParameterizedTest(name = "case: {2}")
    @MethodSource("provideArguments")
    public void emailNotificationJobTest(List<Match> testData, EmailServiceChecker emailServiceChecker,
                                         String description)
    {
        when(matchRepository.findNewMatches()).thenReturn(testData);

        emailNotificationJob.notifyUsersAboutNewMatches();

        emailServiceChecker.checkEmailService(emailService);
    }

    interface EmailServiceChecker {
        void checkEmailService(EmailService emailService);
    }

    public static Stream<Arguments> provideArguments() {
        Match neverNotify = new Match();
        neverNotify.setFirstUser(getNewUser());
        neverNotify.getFirstUser().getNotifications().setNotificationFrequency(NotificationFrequency.NEVER);
        neverNotify.setFirstUserState(MatchState.NEW);
        neverNotify.setSecondUser(getNewUser());
        neverNotify.setSecondUserState(MatchState.ACCEPTED);

        Match firstTimeLetter = new Match();
        firstTimeLetter.setFirstUser(getNewUser());
        firstTimeLetter.setFirstUserState(MatchState.NEW);
        firstTimeLetter.getFirstUser().getNotifications().setNotificationFrequency(NotificationFrequency.ONE_HOUR);
        firstTimeLetter.setSecondUser(getNewUser());
        neverNotify.setSecondUserState(MatchState.ACCEPTED);

        Match sendLetterAfterOneHour = new Match();
        sendLetterAfterOneHour.setFirstUser(getNewUser());
        sendLetterAfterOneHour.setFirstUserState(MatchState.NEW);
        sendLetterAfterOneHour.getFirstUser().getNotifications().setNotificationFrequency(NotificationFrequency.ONE_HOUR);
        sendLetterAfterOneHour.getFirstUser().getNotifications().setLastSentTime(LocalDateTime.now().minus(1,
                ChronoUnit.HOURS
        ));
        sendLetterAfterOneHour.setSecondUser(getNewUser());
        sendLetterAfterOneHour.setSecondUserState(MatchState.ACCEPTED);

        Match doNotSendLetterIfLessThanHour = new Match();
        doNotSendLetterIfLessThanHour.setFirstUser(getNewUser());
        doNotSendLetterIfLessThanHour.setFirstUserState(MatchState.NEW);
        doNotSendLetterIfLessThanHour.getFirstUser().getNotifications().setNotificationFrequency(NotificationFrequency.ONE_HOUR);
        doNotSendLetterIfLessThanHour.getFirstUser().getNotifications().setLastSentTime(LocalDateTime.now().minus(30,
                ChronoUnit.MINUTES
        ));

        doNotSendLetterIfLessThanHour.setSecondUser(getNewUser());
        doNotSendLetterIfLessThanHour.setSecondUserState(MatchState.ACCEPTED);

        Match sendLetterAfterOneDay = new Match();
        sendLetterAfterOneDay.setFirstUser(getNewUser());
        sendLetterAfterOneDay.setFirstUserState(MatchState.NEW);
        sendLetterAfterOneDay.getFirstUser().getNotifications().setNotificationFrequency(NotificationFrequency.ONE_DAY);
        sendLetterAfterOneDay.getFirstUser().getNotifications().setLastSentTime(LocalDateTime.now().minus(1,
                ChronoUnit.DAYS
        ));

        sendLetterAfterOneDay.setSecondUser(getNewUser());
        sendLetterAfterOneDay.setSecondUserState(MatchState.ACCEPTED);

        Match doNotSendLetterIfLessThanOneDay = new Match();
        doNotSendLetterIfLessThanOneDay.setFirstUser(getNewUser());
        doNotSendLetterIfLessThanOneDay.setFirstUserState(MatchState.NEW);
        doNotSendLetterIfLessThanOneDay.getFirstUser().getNotifications().setNotificationFrequency(NotificationFrequency.ONE_DAY);
        doNotSendLetterIfLessThanOneDay.getFirstUser().getNotifications().setLastSentTime(LocalDateTime.now().minus(20, ChronoUnit.HOURS));

        doNotSendLetterIfLessThanOneDay.setSecondUser(getNewUser());
        doNotSendLetterIfLessThanOneDay.setSecondUserState(MatchState.ACCEPTED);


        Match sendNotificationToBothUsers = new Match();
        sendNotificationToBothUsers.setFirstUser(getNewUser());
        sendNotificationToBothUsers.setFirstUserState(MatchState.NEW);
        sendNotificationToBothUsers.getFirstUser().getNotifications().setNotificationFrequency(NotificationFrequency.ONE_DAY);
        sendNotificationToBothUsers.setSecondUser(getNewUser());
        sendNotificationToBothUsers.setSecondUserState(MatchState.NEW);
        sendNotificationToBothUsers.getSecondUser().getNotifications().setNotificationFrequency(NotificationFrequency.ONE_DAY);

        return Stream.of(
                Arguments.of(asList(neverNotify), (EmailServiceChecker) emailService -> verify(emailService,
                        times(0)
                        ).sendEmail(any(), any(), any()), "never send email, to user that doesn't want it"
                ),

                Arguments.of(asList(firstTimeLetter), (EmailServiceChecker) emailService -> verify(emailService,
                        times(1)
                        ).sendEmail(eq(firstTimeLetter.getFirstUser().getEmail()), any(), any()),
                        "must send letter first time"
                ),

                Arguments.of(asList(sendLetterAfterOneHour), (EmailServiceChecker) emailService -> verify(emailService,
                        times(1)
                        ).sendEmail(eq(sendLetterAfterOneHour.getFirstUser().getEmail()), any(), any()),
                        "send letter after one hour"
                ),

                Arguments.of(asList(doNotSendLetterIfLessThanHour),
                        (EmailServiceChecker) emailService -> verify(emailService,
                                times(0)
                        ).sendEmail(any(), any(), any()),
                        "do not send letter if less that hour for one hour preferences"
                ),

                Arguments.of(asList(sendLetterAfterOneDay), (EmailServiceChecker) emailService -> verify(emailService,
                        times(1)
                        ).sendEmail(eq(sendLetterAfterOneDay.getFirstUser().getEmail()), any(), any()),
                        "send letter after 1 day"
                ),
                Arguments.of(asList(doNotSendLetterIfLessThanOneDay),
                        (EmailServiceChecker) emailService -> verify(emailService,
                                times(0)
                        ).sendEmail(any(), any(), any()),
                        "do not send letter if less than 1 day for 1 day preferences"
                ),
                Arguments.of(asList(sendNotificationToBothUsers),
                        (EmailServiceChecker) emailService -> verify(emailService,
                                times(2)
                        ).sendEmail(any(), any(), any()),
                        "must send notifications to both users"
                )
        );
    }


    private static User getNewUser() {
        User user = new User();
        user.setId(userIdCounter++);
        user.setEmail(format("email%s@gmail.com", userIdCounter));
        user.setName("user" + userIdCounter);
        user.setNotifications(new Notifications());
        return user;
    }
}

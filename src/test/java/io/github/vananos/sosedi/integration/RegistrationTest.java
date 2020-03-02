package io.github.vananos.sosedi.integration;

import com.icegreen.greenmail.util.GreenMail;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.registration.RegistrationRequest;
import io.github.vananos.sosedi.repository.UserRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static io.github.vananos.sosedi.models.NotificationFrequency.ONE_DAY;
import static io.github.vananos.sosedi.models.User.UserStatus.EMAIL_UNCONFIRMED;
import static io.github.vananos.sosedi.utils.Constants.INVALID_EMAIL;
import static io.github.vananos.sosedi.utils.RegistrationRequestBag.getRegistrationRequest;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@ExtendWith(value = {SpringExtension.class})
public class RegistrationTest {
    private static final String REGISTRATION_ENDPOINT = "/register";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private GreenMail mailServer;

    @BeforeEach
    public void setUp() {
        mailServer = new GreenMail();
        mailServer.start();
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    @AfterEach
    public void tearDown() {
        mailServer.stop();
    }

    @Test
    public void userIsSuccessfullyRegistered_whenValidRegistrationDataProvided() throws MessagingException, IOException {
        //given
        RegistrationRequest validRegistrationRequest = getRegistrationRequest().build();
        //and
        User expectedUser = new User().setName(validRegistrationRequest.getName())
                .setSurname(validRegistrationRequest.getSurname())
                .setEmail(validRegistrationRequest.getEmail())
                .setUserStatus(EMAIL_UNCONFIRMED);
        //when
        given().contentType(JSON)
                .body(validRegistrationRequest)
                .when()
                .post(REGISTRATION_ENDPOINT)
                .then()
                .status(OK)
                .body("errors", nullValue())
                .body("data", nullValue());
        //then
        User user = userRepository.getUserByEmail(validRegistrationRequest.getEmail());
        assertThat(user).isEqualToIgnoringNullFields(expectedUser);
        assertThat(user.getNotifications().getNotificationFrequency()).isEqualTo(ONE_DAY);
        //and
        confirmationEmailWasSent(user.getEmail(), user.getPinCode());
    }

    @Test
    public void userIsNotRegistered_whenInvalidRegistrationDataProvided() throws InterruptedException {
        //given
        RegistrationRequest invalidRegistrationRequest = getRegistrationRequest()
                .email(INVALID_EMAIL)
                .build();
        //when
        given().contentType(JSON)
                .body(invalidRegistrationRequest)
                .when()
                .post(REGISTRATION_ENDPOINT)
                .then()
                .status(BAD_REQUEST);
        //then
        sleep(1000);
        assertThat(mailServer.getReceivedMessages().length).isZero();
    }

    private void confirmationEmailWasSent(String email, String encodedPinCode) throws MessagingException, IOException {
        await().atMost(1, SECONDS).until(() -> mailServer.getReceivedMessages().length != 0);
        MimeMessage[] messages = mailServer.getReceivedMessages();
        assertThat(messages).hasSize(1);

        MimeMessage emailConfirmationMessage = messages[0];
        assertThat(emailConfirmationMessage.getAllRecipients()).hasSize(1);
        assertThat(emailConfirmationMessage.getAllRecipients()[0].toString()).isEqualTo(email);
        assertThat((String) emailConfirmationMessage.getContent()).contains("Ваша регистрация прошла успешно!");
    }
}
package io.github.vananos.sosedi.integration;

import com.icegreen.greenmail.util.GreenMail;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.UserRepository;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static io.github.vananos.sosedi.utils.Constants.VALID_EMAIL;
import static io.github.vananos.sosedi.utils.UserBag.getUser;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@ExtendWith(value = {SpringExtension.class, MockitoExtension.class})
public class PinCodeRecoveryTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private User user = getUser();

    private GreenMail mailServer;

    @BeforeEach
    public void setUp() {
        mailServer = new GreenMail();
        mailServer.start();
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    @Test
    public void letterWithNewPinCodeSent_whenValidRequestProvided() throws MessagingException, IOException {
        //given
        userRepository.save(user);
        //when
        given().contentType(ContentType.JSON)
                .formParam("email", VALID_EMAIL)
                .post("/requestreset")
                .then()
                .status(HttpStatus.OK);
        //then
        emailWithNewPinCodeWasSent();
    }

    private void emailWithNewPinCodeWasSent() throws MessagingException, IOException {
        await().atMost(1, SECONDS).until(() -> mailServer.getReceivedMessages().length != 0);
        MimeMessage[] messages = mailServer.getReceivedMessages();
        assertThat(messages).hasSize(1);

        MimeMessage emailConfirmationMessage = messages[0];
        assertThat(emailConfirmationMessage.getAllRecipients()).hasSize(1);
        assertThat(emailConfirmationMessage.getAllRecipients()[0].toString()).isEqualTo(user.getEmail());
        assertThat((String) emailConfirmationMessage.getContent()).contains("Ваш новый код для входа");
    }
}

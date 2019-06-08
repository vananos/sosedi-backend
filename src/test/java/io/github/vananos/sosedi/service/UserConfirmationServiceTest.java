package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserConfirmationServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserConfirmationService userConfirmationService;

    @Test
    public void confirmEmailExistingCode_success() {
        User expectedUser = new User();
        expectedUser.setName("test");
        when(userRepository.findByEmailConfirmationId("confirmationCode")).thenReturn(Optional.of(expectedUser));
        Optional<User> resultUser = userConfirmationService.confirmEmail("confirmationCode");
        assertThat(resultUser)
                .isNotEmpty()
                .get();


        assertThat(expectedUser.getUserStatus()).isEqualTo(User.UserStatus.EMAIL_CONFIRMED);

        verify(userRepository, times(1)).save(eq(expectedUser));
    }

    //    @Test
    public void sendConfirmationLetter() {
        User user = new User();
        user.setEmailConfirmationId("confirmationId");
        user.setEmail("van8025@yandex.ru");
        user.setName("Ваня");
        userConfirmationService.sendConfirmationLetter(user);
    }
}
package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.exceptions.UserAlreadyExists;
import io.github.vananos.sosedi.exceptions.UserNotFoundException;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void registerNewUser_successPasswordIsEncoded() {
        User validUser = getValidUser();
        when(userRepository.save(validUser)).thenReturn(validUser);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        userService.registerUser(validUser);

        User expectedUser = getValidUser();
        expectedUser.setPassword(passwordEncoder.encode(validUser.getPassword()));

        verify(userRepository, times(1)).save(eq(expectedUser));
    }

    @Test
    public void registerUserWithExistingEmail_userAlreadyExistsError() {
        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        assertThrows(UserAlreadyExists.class, () -> {
            userService.registerUser(getValidUser());
        });
    }

    @Test
    public void findUnExistingUser_userNotFoundError() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.findUserById(1L);
        });
    }

    @Test
    public void updateUnExistingUser_userNotFoundError() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUserInfo(getValidUser());
        });
    }

    @Test
    public void confirmEmailExistingCode_success() {
        User expectedUser = new User();
        expectedUser.setName("test");
        when(userRepository.findByEmailConfirmationId("confirmationCode")).thenReturn(Optional.of(expectedUser));
        Optional<User> resultUser = userService.confirmEmail("confirmationCode");
        assertThat(resultUser)
                .isNotEmpty()
                .get();


        assertThat(expectedUser.getUserStatus()).isEqualTo(User.UserStatus.EMAIL_CONFIRMED);

        verify(userRepository, times(1)).save(eq(expectedUser));

    }

    private User getValidUser() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setName("testUser");
        expectedUser.setSurname("testSurName");
        expectedUser.setPassword("somePassword");
        expectedUser.setUserStatus(User.UserStatus.EMAIL_UNCONFIRMED);
        expectedUser.setBirthday(LocalDate.of(2000, 11, 21));
        expectedUser.setDescription("About myself");
        expectedUser.setEmail("someemail@gmail.com");
        expectedUser.setPhone("+7-(999)-222-33-44");
        expectedUser.setInterests("");
        return expectedUser;
    }
}

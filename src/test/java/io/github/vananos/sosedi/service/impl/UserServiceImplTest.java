package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.exceptions.UserAlreadyExistsException;
import io.github.vananos.sosedi.exceptions.UserNotFoundException;
import io.github.vananos.sosedi.models.RegistrationInfo;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.events.NewPinCodeRequestedEvent;
import io.github.vananos.sosedi.models.events.UserRegisteredEvent;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static io.github.vananos.sosedi.models.User.UserStatus.EMAIL_CONFIRMED;
import static io.github.vananos.sosedi.models.User.UserStatus.EMAIL_UNCONFIRMED;
import static io.github.vananos.sosedi.utils.Constants.*;
import static io.github.vananos.sosedi.utils.NotificationsBag.getNotifications;
import static io.github.vananos.sosedi.utils.RegistrationInfoBag.getRegistrationInfo;
import static io.github.vananos.sosedi.utils.UserBag.getUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static final String EXISTING_CONFIRMATION_ID = "existingConfirmationId";
    private static final String NONEXISTENT_CONFIRMATION_ID = "nonExistentConfirmationId";

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private PasswordEncoder passwordEncoderMock;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepositoryMock, passwordEncoderMock, applicationEventPublisher);
    }

    @Test
    public void registerUser_shouldRegisterUserAndSendConfirmationLetter() {
        //given
        when(passwordEncoderMock.encode(any())).thenReturn(ENCODED_PINCODE);
        //and
        RegistrationInfo validRegistrationInfo = getRegistrationInfo().build();
        //and
        User expectedUser = new User().setName(validRegistrationInfo.getName())
                .setSurname(validRegistrationInfo.getSurname())
                .setEmail(validRegistrationInfo.getEmail())
                .setUserStatus(EMAIL_UNCONFIRMED)
                .setNotifications(getNotifications())
                .setPinCode(ENCODED_PINCODE);
        //when
        userService.registerUser(validRegistrationInfo);
        //then
        ArgumentCaptor<String> pinCodeArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoderMock).encode(pinCodeArgumentCaptor.capture());
        //and
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryMock).save(userArgumentCaptor.capture());
        verifyNoMoreInteractions(userRepositoryMock);
        //and
        User savedUser = userArgumentCaptor.getValue();
        assertThat(savedUser).isEqualToIgnoringNullFields(expectedUser);
        assertThat(savedUser.getEmailConfirmationId()).isNotEmpty();
        //and
        UserRegisteredEvent expectedUserRegisteredEvent = UserRegisteredEvent.builder()
                .username(VALID_USERNAME)
                .email(VALID_EMAIL)
                .pinCode(pinCodeArgumentCaptor.getValue())
                .emailConfirmationId(savedUser.getEmailConfirmationId())
                .build();
        verify(applicationEventPublisher).publishEvent(expectedUserRegisteredEvent);
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    public void registerUserWithExistingEmail_userAlreadyExistsError() {
        //given
        when(userRepositoryMock.save(any())).thenThrow(DataIntegrityViolationException.class);
        //then
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(getRegistrationInfo().build());
        });
        //and
        verifyZeroInteractions(userRepositoryMock);
        verifyZeroInteractions(applicationEventPublisher);
    }

    @Test
    public void findUnExistingUser_userNotFoundError() {
        when(userRepositoryMock.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.findUserById(1L);
        });
    }

    @Test
    public void confirmEmail_shouldChangeUserStatus_whenUserWasFound() {
        //given
        when(userRepositoryMock.findByEmailConfirmationId(EXISTING_CONFIRMATION_ID)).thenReturn(getUser());
        //and
        User expectedUser = getUser().setUserStatus(EMAIL_CONFIRMED);
        //when
        Optional<User> resultUser = userService.confirmEmail(EXISTING_CONFIRMATION_ID);
        //then
        assertThat(resultUser).contains(expectedUser);
        //and
        verify(userRepositoryMock).save(expectedUser);
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void confirmEmail_shouldDoNothing_whenUserWasNotFound() {
        //given
        when(userRepositoryMock.findByEmailConfirmationId(NONEXISTENT_CONFIRMATION_ID)).thenReturn(null);
        //when
        Optional<User> resultUser = userService.confirmEmail(NONEXISTENT_CONFIRMATION_ID);
        //then
        assertThat(resultUser).isEmpty();
        //and
        verifyZeroInteractions(userRepositoryMock);
    }

    @Test
    public void cancelConfirmation_deleteUser_whenUserWasFound() {
        //given
        when(userRepositoryMock.findByEmailConfirmationId(EXISTING_CONFIRMATION_ID)).thenReturn(getUser());
        //when
        boolean result = userService.cancelConfirmation(EXISTING_CONFIRMATION_ID);
        //then
        assertThat(result).isTrue();
        //and
        verify(userRepositoryMock).delete(getUser());
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void cancelConfirmation_doNothing_whenUserWasNotFound() {
        //given
        when(userRepositoryMock.findByEmailConfirmationId(NONEXISTENT_CONFIRMATION_ID)).thenReturn(null);
        //when
        boolean result = userService.cancelConfirmation(NONEXISTENT_CONFIRMATION_ID);
        //then
        assertThat(result).isFalse();
        //and
        verifyZeroInteractions(userRepositoryMock);
    }

    @ParameterizedTest
    @EnumSource(value = User.UserStatus.class, names = {"EMAIL_CONFIRMED", "PROFILE_FILLED", "AD_FILLED"})
    public void cancelConfirmation_doNothing_whenUserStatusIsOtherThanEmailUnConfirmed(User.UserStatus userStatusUnavailableForConfirmation) {
        //given
        when(userRepositoryMock.findByEmailConfirmationId(EXISTING_CONFIRMATION_ID)).thenReturn(
                getUser()
                        .setUserStatus(userStatusUnavailableForConfirmation));
        //when
        boolean result = userService.cancelConfirmation(EXISTING_CONFIRMATION_ID);
        //then
        assertThat(result).isFalse();
        //and
        verifyZeroInteractions(userRepositoryMock);
    }

    @Test
    public void requestNewPinCodeFor_shouldUpdatePinCodeAndSendLetterWithNewPinCode_whenUserFoundByEmail() {
        //given
        when(userRepositoryMock.getUserByEmail(VALID_EMAIL)).thenReturn(getUser());
        //and
        String newEncodedPinCode = "newEncodedPinCode";
        //and
        when(passwordEncoderMock.encode(any())).thenReturn(newEncodedPinCode);
        //when
        boolean wasSuccessful = userService.requestNewPinCodeFor(VALID_EMAIL);
        //then
        assertThat(wasSuccessful).isTrue();
        //and
        verify(userRepositoryMock).save(getUser().setPinCode(newEncodedPinCode));
        verifyNoMoreInteractions(userRepositoryMock);
        //and
        ArgumentCaptor<String> newPinCodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoderMock).encode(newPinCodeCaptor.capture());
        //and
        verify(applicationEventPublisher).publishEvent(NewPinCodeRequestedEvent.builder()
                .email(VALID_EMAIL)
                .username(VALID_USERNAME)
                .pinCode(newPinCodeCaptor.getValue())
                .build());
        verifyNoMoreInteractions(applicationEventPublisher);
    }

    @Test
    public void requestNewPinCodeFor_shouldDoNothing_whenUserNotFound() {
        //given
        when(userRepositoryMock.getUserByEmail(VALID_EMAIL)).thenReturn(null);
        //when
        boolean wasSuccessful = userService.requestNewPinCodeFor(VALID_EMAIL);
        //then
        assertThat(wasSuccessful).isFalse();
        //and
        verifyZeroInteractions(userRepositoryMock);
        //and
        verifyZeroInteractions(applicationEventPublisher);
    }
}
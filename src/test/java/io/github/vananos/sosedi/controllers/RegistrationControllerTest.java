package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.components.validation.RequestValidator;
import io.github.vananos.sosedi.exceptions.BadParametersException;
import io.github.vananos.sosedi.exceptions.UserAlreadyExistsException;
import io.github.vananos.sosedi.models.RegistrationInfo;
import io.github.vananos.sosedi.models.dto.BaseResponse2;
import io.github.vananos.sosedi.models.dto.registration.RegistrationRequest;
import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static io.github.vananos.sosedi.utils.RegistrationRequestBag.getRegistrationRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationControllerTest {

    @Mock
    private UserService userServiceMock;

    @Mock
    private RequestValidator requestValidatorMock;

    private RegistrationController registrationController;

    @BeforeEach
    public void setUp() {
        registrationController = new RegistrationController(userServiceMock, requestValidatorMock);
    }

    @Test
    public void register_shouldRegisterUserWhenValidInfoProvided() {
        //given
        RegistrationRequest validRegistrationRequest = getRegistrationRequest().build();
        //and
        BindingResult bindingResultWithoutErrors = mock(BindingResult.class);
        //and
        ResponseEntity<BaseResponse2> expectedResult = ResponseEntity.ok(BaseResponse2.builder().build());
        //when
        ResponseEntity<BaseResponse2> actualResult = registrationController.register(validRegistrationRequest, bindingResultWithoutErrors);
        //then
        assertThat(actualResult).isEqualTo(expectedResult);
        //and
        verify(requestValidatorMock).assertValid(bindingResultWithoutErrors);
        verifyNoMoreInteractions(requestValidatorMock);
        //and
        verify(userServiceMock).registerUser(RegistrationInfo.builder()
                .name(validRegistrationRequest.getName())
                .surname(validRegistrationRequest.getSurname())
                .email(validRegistrationRequest.getEmail())
                .build());
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void register_shouldRethrowExceptionFromRequestValidator() {
        //given
        doThrow(new BadParametersException()).when(requestValidatorMock).assertValid(any());
        //then
        assertThrows(BadParametersException.class, () -> {
            registrationController.register(getRegistrationRequest().build(), mock(BindingResult.class));
        });
        //and
        verifyZeroInteractions(userServiceMock);
    }

    @Test
    public void register_shouldRethrowExceptionFromUserServiceWhenUserAlreadyExists() {
        //given
        doThrow(new UserAlreadyExistsException()).when(userServiceMock).registerUser(any());
        //then
        assertThrows(UserAlreadyExistsException.class, () -> {
            registrationController.register(getRegistrationRequest().build(), mock(BindingResult.class));
        });
    }
}
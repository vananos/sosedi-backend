package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.exceptions.UserAlreadyExistsException;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.registration.RegistrationRequest;
import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.stream.Stream;

import static io.github.vananos.Utils.toJson;
import static io.github.vananos.sosedi.controllers.RegistrationController.USER_ALREADY_EXISTS;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class RegistrationControllerTest {
    private static final String REGISTRATION_ENDPOINT = "/register";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;


    @Test
    public void register_userRegisteredSuccessfully() throws Exception {
        doNothing().when(userService).registerUser(any(User.class));

        RegistrationRequest validUserRegistrationRequest = validUserRegistrationRequest();

        mvc.perform(
                registrationPost()
                        .content(toJson(validUserRegistrationRequest)))
                .andExpect(status().isOk());

        User userRegistrationInfo = new User();
        userRegistrationInfo.setName(validUserRegistrationRequest.getName());
        userRegistrationInfo.setSurname(validUserRegistrationRequest.getSurname());
        userRegistrationInfo.setPassword(validUserRegistrationRequest.getPassword());
        userRegistrationInfo.setEmail(validUserRegistrationRequest.getEmail());

        verify(userService, times(1)).registerUser(Matchers.eq(userRegistrationInfo));
    }

    @Test
    public void register_userAlreadyExists() throws Exception {
        doThrow(new UserAlreadyExistsException()).when(userService).registerUser(any(User.class));

        mvc.perform(
                registrationPost()
                        .content(toJson(validUserRegistrationRequest())))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description", is(USER_ALREADY_EXISTS)));
    }

    @ParameterizedTest
    @MethodSource("provideValidationErrorsArguments")
    public void register_validationErrors(RegistrationRequest registrationRequest, String errorId) throws Exception {
        mvc.perform(
                registrationPost()
                        .content(toJson(registrationRequest)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].id", is(errorId)));

        verify(userService, never()).registerUser(any());
    }

    public static Stream<Arguments> provideValidationErrorsArguments() {
        RegistrationRequest withInvalidName = validUserRegistrationRequest();
        withInvalidName.setName("_");

        RegistrationRequest withInvalidSurname = validUserRegistrationRequest();
        withInvalidSurname.setSurname("_");

        RegistrationRequest withInvalidEmail = validUserRegistrationRequest();
        withInvalidEmail.setEmail("invalidEmail.com");

        RegistrationRequest withWeakPassword = validUserRegistrationRequest();
        withWeakPassword.setPassword("weakPassword");

        return Stream.of(
                Arguments.of(withInvalidName, "name"),
                Arguments.of(withInvalidSurname, "surname"),
                Arguments.of(withInvalidEmail, "email"),
                Arguments.of(withWeakPassword, "password")
        );
    }

    private MockHttpServletRequestBuilder registrationPost() {
        return post(REGISTRATION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8");
    }

    private static RegistrationRequest validUserRegistrationRequest() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("test@gmail.com");
        registrationRequest.setPassword("VeryStrongPassword_130");
        registrationRequest.setName("username");
        registrationRequest.setSurname("usersurname");
        return registrationRequest;
    }
}
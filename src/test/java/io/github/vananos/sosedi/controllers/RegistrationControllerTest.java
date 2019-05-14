package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.BaseResponse;
import io.github.vananos.sosedi.models.registration.RegistrationRequest;
import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static io.github.vananos.Utils.toJson;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {

    private static final String REGISTRATION_ENDPOINT = "/register";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;


    @Test
    public void register_userRegisteredSuccessfully() throws Exception {
        mvc.perform(post(REGISTRATION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(toJson(
                        new RegistrationRequest()
                                .email("test@gmail.com")
                                .password("VeryStrongPassword_130")
                                .passwordConfirmation("VeryStrongPassword_130")
                                .name("username")
                                .surname("usersurname"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(BaseResponse.ResultStatus.SUCCESS.toString())));
    }
}

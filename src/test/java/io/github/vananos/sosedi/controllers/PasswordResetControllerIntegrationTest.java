package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.passwordreset.PasswordResetRequest;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.EmailService;
import io.github.vananos.sosedi.service.PasswordResetService;
import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static io.github.vananos.Utils.getValidUser;
import static io.github.vananos.Utils.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PasswordResetControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordResetService passwordResetService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void passwordResetIntegrationTest() throws Exception {

        User user = getValidUser();

        when(userRepository.getUserByEmail(any())).thenReturn(user);

        mvc.perform(post("/requestreset").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .param("email", user.getEmail()))
                .andExpect(status().isOk());

        when(passwordEncoder.encode(any())).thenReturn("NEWPASSWORD_ENCODED");

        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setPassword("NEWPASSWORD1");

        mvc.perform(post("/resetpassword").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(toJson(passwordResetRequest)))
                .andExpect(status().isOk());

        User expectedUser = getValidUser();
        expectedUser.setPinCode("NEWPASSWORD_ENCODED");

        verify(userService, times(1)).updateUserInfo(eq(expectedUser));
    }
}
package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class EmailConfirmationControllerTest {

    public static final String EMAIL_CONFIRMATION_URL = "/confirmation/confirmationCode";

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void emailConfirmation_success() throws Exception {
        User expectedUser = new User();
        expectedUser.setName("test");
        when(userService.confirmEmail(eq("confirmationCode"))).thenReturn(Optional.of(expectedUser));

        mvc.perform(MockMvcRequestBuilders.get(EMAIL_CONFIRMATION_URL))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/?userid=" + URLEncoder.encode(expectedUser.getName(), StandardCharsets.UTF_8.toString())));
    }


    @Test
    public void emailConfirmation_notExistingConfirmationId() throws Exception {
        User expectedUser = new User();
        expectedUser.setName("test");
        when(userService.confirmEmail(eq("confirmationCode"))).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.get(EMAIL_CONFIRMATION_URL))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/error"));
    }
}
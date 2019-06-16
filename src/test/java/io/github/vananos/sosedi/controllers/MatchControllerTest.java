package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.security.UserDetailsImpl;
import io.github.vananos.sosedi.service.MatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static io.github.vananos.Utils.getValidUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MatchControllerTest {
    private static final String MATCHES_ENDPOINT = "/matches";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MatchService matchService;

    @Test
    public void userGetsOwnMatches_success() throws Exception {
        mvc.perform(get(MATCHES_ENDPOINT + "?userid=1").with(user(new UserDetailsImpl(getValidUser()))))
                .andExpect(status().isOk());
    }

    @Test
    public void userGetsAnotherUserMatches_unauthorized() throws Exception {
        mvc.perform(get(MATCHES_ENDPOINT + "?userid=2").with(user(new UserDetailsImpl(getValidUser()))))
                .andExpect(status().isForbidden());
    }
}
package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.NotificationFrequency;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.settings.ChangeNotificationSettingsRequest;
import io.github.vananos.sosedi.models.dto.settings.ChangePasswordRequest;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static io.github.vananos.Utils.getValidUser;
import static io.github.vananos.Utils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class SettingsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void ownerCanChangeOwnPasswordTest() throws Exception {
        User user = getValidUser();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setUserId(user.getId());
        changePasswordRequest.setPassword("New_Password_1");

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(getValidUser()));
        when(passwordEncoder.encode(any())).thenReturn("encoded_pass");
        when(userRepository.save(any())).thenReturn(user);

        mvc.perform(post("/settings/pincode")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .with(user(new UserDetailsImpl(user)))
                .content(toJson(changePasswordRequest)))
                .andExpect(status().isOk());

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository, times(1)).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getPincode()).isEqualTo("encoded_pass");
    }


    @Test
    public void notOwnerCouldNotChangePasswordTest() throws Exception {
        User user = getValidUser();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setUserId(user.getId() + 1);
        changePasswordRequest.setPassword("New_Password_1");

        mvc.perform(post("/settings/pincode")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .with(user(new UserDetailsImpl(user)))
                .content(toJson(changePasswordRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void ownerCanChangeNotificationSettings() throws Exception {
        User user = getValidUser();

        ChangeNotificationSettingsRequest req = new ChangeNotificationSettingsRequest();
        req.setUserId(user.getId());
        req.setNotificationFrequency(NotificationFrequency.NEVER);

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(getValidUser()));
        when(userRepository.save(any())).thenReturn(user);

        mvc.perform(post("/settings/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .with(user(new UserDetailsImpl(user)))
                .content(toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    public void notOwnerCanNotChangeNotificationSettings() throws Exception {
        User user = getValidUser();

        ChangeNotificationSettingsRequest req = new ChangeNotificationSettingsRequest();
        req.setUserId(user.getId() + 1);
        req.setNotificationFrequency(NotificationFrequency.NEVER);

        mvc.perform(post("/settings/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .with(user(new UserDetailsImpl(user)))
                .content(toJson(req)))
                .andExpect(status().isForbidden());
    }
}

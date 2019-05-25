package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.exceptions.UserNotFoundException;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.userprofile.UserProfileInfo;
import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.Collections;

import static io.github.vananos.Utils.toJson;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserProfileControllerTest {
    private static final String USER_PROFILE_ENDPOINT = "/profile";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    public void getExistingProfile_successResponse() throws Exception {
        User validUser = getValidUser();

        when(userService.findUserById(1L)).thenReturn(validUser);

        mvc.perform(get(USER_PROFILE_ENDPOINT + "?userid=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.userId", is(1)))
                .andExpect(jsonPath("data.name", is(validUser.getName())))
                .andExpect(jsonPath("data.surname", is(validUser.getSurname())))
                .andExpect(jsonPath("data.birthday", is(validUser.getBirthday().toString())))
                .andExpect(jsonPath("data.description", is(validUser.getDescription())))
                .andExpect(jsonPath("data.phone", is(validUser.getPhone())))
                .andExpect(jsonPath("data.isNewUser", is(true)));
    }

    @Test
    public void getUnExistingProfile_notFoundResponse() throws Exception {
        when(userService.findUserById(1L)).thenThrow(new UserNotFoundException());

        mvc.perform(get(USER_PROFILE_ENDPOINT + "?userid=1"))

                .andExpect(status().isNotFound());
    }


    @Test
    public void postValidUpdateInfo_profileUpdated() throws Exception {
        when(userService.updateUserInfo(any())).thenReturn(null);

        mvc.perform(updatePost().content(toJson(getValidUserProfileInfo())))
                .andExpect(status().isOk());

        User expectedUser = getValidUser();
        expectedUser.setEmail(null);
        expectedUser.setInterests("");
        expectedUser.setPassword(null);
        expectedUser.setUserStatus(User.UserStatus.PROFILE_FILLED);

        verify(userService, times(1)).updateUserInfo(eq(expectedUser));
    }

    @Test
    public void postUnExistingUserProfileUpdate_notFoundResponse() throws Exception {
        when(userService.updateUserInfo(any())).thenThrow(new UserNotFoundException());

        mvc.perform(updatePost().content(toJson(getValidUserProfileInfo())))
                .andExpect(status().isNotFound());
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

    private UserProfileInfo getValidUserProfileInfo() {
        UserProfileInfo userProfileInfo = new UserProfileInfo();
        User validUser = getValidUser();
        userProfileInfo.setName(validUser.getName());
        userProfileInfo.setSurname(validUser.getSurname());
        userProfileInfo.setBirthday(validUser.getBirthday());
        userProfileInfo.setDescription(validUser.getDescription());
        userProfileInfo.setPhone(validUser.getPhone());
        userProfileInfo.setId(validUser.getId());
        userProfileInfo.setInterests(Collections.emptyList());
        return userProfileInfo;
    }

    private MockHttpServletRequestBuilder updatePost() {
        return post(USER_PROFILE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8");
    }
}

package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.service.FileService;
import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class ImageControllerTest {
    private static final String IMAGE_UPLOAD_ENDPOINT = "/photo";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private UserService userService;


    @Test
    public void postImage_acceptImage() throws Exception {
        byte[] image = Files.readAllBytes(
                Paths.get(
                        getClass().getClassLoader().getResource("imgtest/sun.png").getPath()));
        String expectedFileName = UUID.randomUUID().toString() + ".png";
        when(fileService.saveFile(any())).thenReturn(expectedFileName);


        mvc.perform(MockMvcRequestBuilders
                .fileUpload(IMAGE_UPLOAD_ENDPOINT).file(new MockMultipartFile("file", image)).param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.name", equalTo(expectedFileName)));

        verify(userService, times(1)).setAvatarForUser(eq(expectedFileName), eq(1L));
        verify(fileService, times(1)).saveFile(any());
    }

    @Test
    public void postNotImage_badRequestError() throws Exception {
        byte[] notImage = Files.readAllBytes(
                Paths.get(
                        getClass().getClassLoader().getResource("imgtest/notimage.txt").getPath()));
        mvc.perform(MockMvcRequestBuilders
                .fileUpload(IMAGE_UPLOAD_ENDPOINT).file(new MockMultipartFile("file", notImage)).param("userId", "1"))
                .andExpect(status().isBadRequest());

        verify(fileService, times(0)).saveFile(any());
    }

}

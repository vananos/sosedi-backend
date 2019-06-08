package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.Advertisement;
import io.github.vananos.sosedi.models.dto.ad.AdResponse;
import io.github.vananos.sosedi.service.AdService;
import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class AdControllerTest {
    private static final String AD_ENDPOINT = "/ad";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AdService adService;

    @Test
    public void getExistingAd_success() throws Exception {
        AdResponse adResponse = new AdResponse();


        when(adService.getAd(1L)).thenReturn(getValidAdResponse());

        mvc.perform(get(AD_ENDPOINT + "?userid=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.userId", is(1)))
                .andExpect(jsonPath("data.name", is(validUser.getName())))
                .andExpect(jsonPath("data.surname", is(validUser.getSurname())))
                .andExpect(jsonPath("data.birthday", is(validUser.getBirthday().toString())))
                .andExpect(jsonPath("data.description", is(validUser.getDescription())))
                .andExpect(jsonPath("data.phone", is(validUser.getPhone())))
                .andExpect(jsonPath("data.isNewUser", is(true)));
    }

    private Advertisement getValidAdverstiment() {
        Advertisement ad = new Advertisement();
        ad.setMale(true);
        ad.setFemale(true);
        adResponse.setAdditionalConveniences(Arrays.asList("tv", "internet"));
        adResponse.setAnimals("ok");
        adResponse.setDescription("description");
        adResponse.setLandLord(true);
        adResponse.setMinAge(35);
        adResponse.setMinAge(20);
        adResponse.setPrice(12000);
        adResponse.setPlaceId("SPB");
        adResponse.setRoomType(Arrays.asList("room"));

    }

    private AdResponse getValidAdResponse() {
        AdResponse adResponse = new AdResponse();
        adResponse.setMale(true);
        adResponse.setFemale(true);
        adResponse.setAdditionalConveniences(Arrays.asList("tv", "internet"));
        adResponse.setAnimals("ok");
        adResponse.setDescription("description");
        adResponse.setLandLord(true);
        adResponse.setMinAge(35);
        adResponse.setMinAge(20);
        adResponse.setPrice(12000);
        adResponse.setPlaceId("SPB");
        adResponse.setRoomType(Arrays.asList("room"));
        return adResponse;
    }

    private MockHttpServletRequestBuilder updatePost() {
        return post(AD_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8");
    }
}

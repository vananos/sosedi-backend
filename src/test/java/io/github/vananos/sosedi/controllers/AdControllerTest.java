package io.github.vananos.sosedi.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.vananos.sosedi.models.Advertisement;
import io.github.vananos.sosedi.models.Attitude;
import io.github.vananos.sosedi.models.Convenience;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.ad.AdRequest;
import io.github.vananos.sosedi.models.dto.ad.AdResponse;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.security.UserDetailsImpl;
import io.github.vananos.sosedi.service.AdService;
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

import static io.github.vananos.Utils.*;
import static io.github.vananos.sosedi.models.RoomType.SINGLE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class AdControllerTest {
    private static final String DESCRIPTION = "description about myself";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AdService adService;

    @Test
    public void userTryToGetAnotherUserAdInfo_accessMustBeDenied() throws Exception {
        User user = getValidUser();

        Advertisement ad = new Advertisement();
        ad.setId(1L);
        user.setAdvertisement(ad);

        mvc.perform(get("/ad?userid=2").with(user(new UserDetailsImpl(user))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void saveNewAd() throws Exception {
        AdRequest adRequest = getValidAdRequestForUser();
        adRequest.setId(null);

        when(adService.saveAdForUser(eq(1L), any())).thenReturn(getValidAdverstiment());

        String jsonResponse = mvc.perform(postAdUpdate()
                .with(user(new UserDetailsImpl(getValidUser())))
                .content(toJson(adRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        BaseResponse<AdResponse> expectedResponse = new BaseResponse().data(getValidAdResponse());

        assertThat(fromJson(jsonResponse, new TypeReference<BaseResponse<AdResponse>>() {
        }))
                .isEqualTo(expectedResponse);
    }

    public static AdRequest getValidAdRequestForUser() {
        AdRequest adRequest = new AdRequest();
        adRequest.setUserId(1L);
        adRequest.setId(1L);

        adRequest.setAnimals(Attitude.GOOD);
        adRequest.setSmoking(Attitude.BAD);
        adRequest.setDescription(DESCRIPTION);
        adRequest.setLandlord(true);
        adRequest.setConveniences(asList(Convenience.TV));
        adRequest.setFemale(true);
        adRequest.setMale(true);
        adRequest.setMinAge(20);
        adRequest.setMaxAge(40);
        adRequest.setRentPay(15000);
        adRequest.setPlaceId("SPB");
        adRequest.setRoomType(asList(SINGLE));
        return adRequest;
    }

    private Advertisement getValidAdverstiment() {
        Advertisement ad = new Advertisement();
        ad.setId(1L);
        ad.setAnimals(Attitude.GOOD);
        ad.setSmoking(Attitude.BAD);
        ad.setDescription(DESCRIPTION);
        ad.setLandlord(true);
        ad.setConveniences(asList(Convenience.TV));
        ad.setMale(true);
        ad.setFemale(true);
        ad.setMinAge(20);
        ad.setMaxAge(40);
        ad.setRentPay(15000);
        ad.setPlaceId("SPB");
        ad.setRoomType(asList(SINGLE));
        return ad;
    }

    private AdResponse getValidAdResponse() {
        AdResponse adResponse = new AdResponse();
        adResponse.setId(1L);
        adResponse.setUserId(1L);
        adResponse.setAnimals(Attitude.GOOD);
        adResponse.setSmoking(Attitude.BAD);
        adResponse.setDescription(DESCRIPTION);
        adResponse.setLandLord(true);
        adResponse.setMale(true);
        adResponse.setFemale(true);
        adResponse.setMinAge(20);
        adResponse.setMaxAge(40);
        adResponse.setRentPay(15000);
        adResponse.setPlaceId("SPB");
        adResponse.setConveniences(asList(Convenience.TV));
        adResponse.setRoomType(asList(SINGLE));
        return adResponse;
    }

    private MockHttpServletRequestBuilder postAdUpdate() {
        return post("/ad").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8");
    }
}

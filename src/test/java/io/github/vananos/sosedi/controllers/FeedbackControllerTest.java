package io.github.vananos.sosedi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vananos.sosedi.models.dto.feedback.FeedbackRequest;
import io.github.vananos.sosedi.models.dto.BaseResponse;
import io.github.vananos.sosedi.models.dto.Error;
import io.github.vananos.sosedi.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static io.github.vananos.Utils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class FeedbackControllerTest {

    public static final String FEEDBACK_ENDPOINT = "/feedback";

    @Autowired
    private MockMvc mvc;

    @Value("${admin.email}")
    private String adminEmail;

    @MockBean
    private EmailService emailService;

    @Test
    public void validFeedBack_success() throws Exception {
        FeedbackRequest validFeedbackRequest = new FeedbackRequest();
        validFeedbackRequest.setEmail("van8025@yandex.ru");
        validFeedbackRequest.setName("test");
        validFeedbackRequest.setMessage("help me!");

        mvc.perform(feedbackPost().content(toJson(validFeedbackRequest)))
                .andExpect(status().isOk());

        verify(emailService, after(1000).times(1)).sendEmail(eq(adminEmail), eq("feedback"), any());
    }

    @Test
    public void invalidFeedBack_error() throws Exception {
        FeedbackRequest invalidFeedbackRequest = new FeedbackRequest();
        invalidFeedbackRequest.setName("t");
        invalidFeedbackRequest.setEmail("invalid");
        invalidFeedbackRequest.setMessage(new String(new char[2048]).replace('\0', 'e'));

        String responseContent = mvc.perform(feedbackPost().content(toJson(invalidFeedbackRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Error> errors = new ObjectMapper().readValue(responseContent, BaseResponse.class).errors();

        assertThat(errors.stream().map(e -> e.id())).containsExactlyInAnyOrder("name", "email", "message");


    }

    private MockHttpServletRequestBuilder feedbackPost() {
        return post(FEEDBACK_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8");
    }

}

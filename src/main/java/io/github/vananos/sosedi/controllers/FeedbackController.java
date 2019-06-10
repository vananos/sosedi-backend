package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.dto.feedback.FeedbackRequest;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static io.github.vananos.sosedi.components.validation.ErrorProcessingUtils.assertHasNoErrors;

@RestController
public class FeedbackController {

    private FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/feedback")
    public ResponseEntity<BaseResponse> feedback(@RequestBody @Valid FeedbackRequest feedbackRequest,
                                                 BindingResult bindingResult) {

        assertHasNoErrors(bindingResult);

        feedbackService.processFeedback(feedbackRequest);
        return ResponseEntity.ok().build();
    }
}

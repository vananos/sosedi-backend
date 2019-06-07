package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.components.validation.ErrorProcessor;
import io.github.vananos.sosedi.models.dto.feedback.FeedbackRequest;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.models.dto.registration.Error;
import io.github.vananos.sosedi.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class FeedbackController {

    private ErrorProcessor errorProcessor;

    private FeedbackService feedbackService;

    @Autowired
    public FeedbackController(ErrorProcessor errorProcessor, FeedbackService feedbackService) {
        this.errorProcessor = errorProcessor;
        this.feedbackService = feedbackService;
    }

    @PostMapping("/feedback")
    public ResponseEntity<BaseResponse> feedback(@RequestBody @Valid FeedbackRequest feedbackRequest,
                                                 BindingResult bindingResult) {

        Optional<List<Error>> validationResult = errorProcessor.handleErrors(bindingResult);

        if (validationResult.isPresent()) {
            return ResponseEntity.badRequest().body(new BaseResponse().errors(validationResult.get()));
        }

        feedbackService.processFeedback(feedbackRequest);
        return ResponseEntity.ok().build();
    }
}

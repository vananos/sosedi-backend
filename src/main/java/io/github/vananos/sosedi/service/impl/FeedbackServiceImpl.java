package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.models.dto.feedback.FeedbackRequest;
import io.github.vananos.sosedi.service.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {
    @Override
    public void processFeedback(FeedbackRequest feedbackRequest) {
        log.info("getting feedback {}", feedbackRequest.toString());
    }
}

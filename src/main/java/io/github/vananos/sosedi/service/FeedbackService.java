package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.dto.feedback.FeedbackRequest;

public interface FeedbackService {
    void processFeedback(FeedbackRequest feedbackRequest);
}

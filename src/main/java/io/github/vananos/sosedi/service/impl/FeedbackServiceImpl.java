package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.models.dto.feedback.FeedbackRequest;
import io.github.vananos.sosedi.service.EmailService;
import io.github.vananos.sosedi.service.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    @Value("${admin.email}")
    private String adminEmail;

    private TemplateEngine templateEngine;

    private EmailService emailService;

    public FeedbackServiceImpl(EmailService emailService, TemplateEngine templateEngine) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    @Override
    public void processFeedback(FeedbackRequest feedbackRequest) {
        log.info("getting feedback {}", feedbackRequest.toString());

        new Thread(
                () -> {
                    Context ctx = new Context();
                    ctx.setVariable("feedback", feedbackRequest);
                    String message = templateEngine.process("feedback", ctx);
                    emailService.sendEmail(adminEmail, "feedback", message);
                }
        ).start();
    }
}

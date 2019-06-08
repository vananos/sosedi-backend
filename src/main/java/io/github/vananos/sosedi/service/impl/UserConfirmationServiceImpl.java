package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.EmailService;
import io.github.vananos.sosedi.service.UserConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;

@Service
public class UserConfirmationServiceImpl implements UserConfirmationService {

    private EmailService emailService;
    private TemplateEngine templateEngine;
    private UserRepository userRepository;

    @Value("${sosedi.hostname}")
    private String hostName;

    @Autowired
    public UserConfirmationServiceImpl(EmailService emailService, TemplateEngine templateEngine, UserRepository userRepository) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.userRepository = userRepository;
    }

    @Override
    public void sendConfirmationLetter(User user) {
        Context ctx = new Context();
        ctx.setVariable("confirmationLink", String.format("%s/confirm/%s", hostName, user.getEmailConfirmationId()));
        ctx.setVariable("username", user.getName());
        String htmlLetter = templateEngine.process("confirmationEmail", ctx);
        emailService.sendEmail(user.getEmail(), "Подтверждение учетной записи", htmlLetter);
    }


    @Override
    public Optional<User> confirmEmail(String confirmationId) {
        Optional<User> user = userRepository.findByEmailConfirmationId(confirmationId);
        if (user.isPresent()) {
            user.get().setUserStatus(User.UserStatus.EMAIL_CONFIRMED);
            userRepository.save(user.get());
        }

        return user;
    }
}
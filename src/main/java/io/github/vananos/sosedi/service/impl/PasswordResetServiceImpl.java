package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.service.EmailService;
import io.github.vananos.sosedi.service.PasswordResetService;
import io.github.vananos.sosedi.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Value("${sosedi.hostname}")
    private String hostName;

    private UserService userService;
    private EmailService emailService;
    private TaskExecutor taskExecutor;
    private TemplateEngine templateEngine;
    private PasswordEncoder passwordEncoder;

    public PasswordResetServiceImpl(UserService userService,
                                    EmailService emailService,
                                    TaskExecutor taskExecutor,
                                    TemplateEngine templateEngine,
                                    PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.taskExecutor = taskExecutor;
        this.templateEngine = templateEngine;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void startPasswordResetForUser(User user) {
        String newPincode = null;
        user.setPinCode(passwordEncoder.encode(newPincode));
        userService.updateUserInfo(user);
        sendPasswordRestoreLetter(user, newPincode);
    }

    private void sendPasswordRestoreLetter(User user, String pincode) {
        taskExecutor.execute(() -> {
            Context ctx = new Context();
            ctx.setVariable("username", user.getName());
            ctx.setVariable("pincode", pincode);
            String letter = templateEngine.process("pincodeRestore", ctx);
            emailService.sendEmail(user.getEmail(), "Восстановление пароля", letter);
        });
    }
}
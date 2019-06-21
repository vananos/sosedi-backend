package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.models.PasswordResetTask;
import io.github.vananos.sosedi.models.PasswordResetTask.PasswordResetTaskStatus;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.PasswordResetRepository;
import io.github.vananos.sosedi.service.EmailService;
import io.github.vananos.sosedi.service.PasswordResetService;
import io.github.vananos.sosedi.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static java.lang.String.format;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Value("${sosedi.hostname}")
    private String hostName;

    private UserService userService;
    private EmailService emailService;
    private PasswordResetRepository passwordResetRepository;
    private TaskExecutor taskExecutor;
    private TemplateEngine templateEngine;
    private PasswordEncoder passwordEncoder;

    public PasswordResetServiceImpl(UserService userService,
                                    EmailService emailService,
                                    PasswordResetRepository passwordResetRepository,
                                    TaskExecutor taskExecutor,
                                    TemplateEngine templateEngine,
                                    PasswordEncoder passwordEncoder)
    {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordResetRepository = passwordResetRepository;
        this.taskExecutor = taskExecutor;
        this.templateEngine = templateEngine;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void resetPassword(String secret, String newPassword) {
        PasswordResetTask passwordResetTask = passwordResetRepository.findBySecret(secret);
        if (passwordResetTask == null) {
            throw new IllegalArgumentException("invalid secret:" + secret);
        }
        if (passwordResetTask.getStatus() != PasswordResetTaskStatus.NEW) {
            return;
        }

        User user = passwordResetTask.getTargetUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUserInfo(user);
        passwordResetTask.setStatus(PasswordResetTaskStatus.RESET);
        passwordResetRepository.save(passwordResetTask);
    }

    @Override
    public void startPasswordResetForUser(User user) {
        PasswordResetTask passwordResetTask = new PasswordResetTask();
        passwordResetTask.setTargetUser(user);
        String secret = generatePasswordResetSecret(user);
        passwordResetTask.setSecret(secret);
        passwordResetTask.setStatus(PasswordResetTaskStatus.NEW);

        passwordResetRepository.findByUserAndStatus(user, PasswordResetTaskStatus.NEW)
                .forEach(task -> {
                    task.setStatus(PasswordResetTaskStatus.CANCELLED);
                    passwordResetRepository.save(task);
                });

        passwordResetRepository.save(passwordResetTask);

        sendPasswordRestoreLetter(user, secret);
    }

    private void sendPasswordRestoreLetter(User user, String secret) {
        taskExecutor.execute(() -> {
            Context ctx = new Context();
            ctx.setVariable("username", user.getName());
            ctx.setVariable("restoreLink", format("%s/restore/%s", hostName, secret));
            String letter = templateEngine.process("passwordRestore", ctx);
            emailService.sendEmail(user.getEmail(), "Восстановление пароля", letter);
        });
    }

    private String generatePasswordResetSecret(User user) {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[16];
        byte[] userPasswordBytes = user.getPassword().getBytes();

        random.nextBytes(randomBytes);

        byte[] bytesForDigest = new byte[16 + userPasswordBytes.length];
        System.arraycopy(randomBytes, 0, bytesForDigest, 0, 16);
        System.arraycopy(userPasswordBytes, 0, bytesForDigest, 16, userPasswordBytes.length);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] resultBytes = digest.digest(bytesForDigest);

        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : resultBytes) {
            stringBuffer.append(Integer.toHexString(b));
        }
        return stringBuffer.toString();
    }
}
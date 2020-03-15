package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.models.events.NewPinCodeRequestedEvent;
import io.github.vananos.sosedi.models.events.UserRegisteredEvent;
import io.github.vananos.sosedi.service.EmailService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static java.lang.String.format;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final ITemplateEngine templateEngine;
    private final String emailFrom;
    private final String hostName;

    @Autowired
    public EmailServiceImpl(@NonNull JavaMailSender emailSender,
                            @NonNull ITemplateEngine templateEngine,
                            @NonNull @Value("${email.from}") String emailFrom,
                            @NonNull @Value("${sosedi.hostname}") String hostName) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.emailFrom = emailFrom;
        this.hostName = hostName;
    }

    @Async
    @Override
    @EventListener
    public void sendEmailConfirmationLetter(@NonNull UserRegisteredEvent userRegisteredEvent) {
        log.debug("Send confirmation letter to {}", userRegisteredEvent.getEmail());
        Context ctx = new Context();
        ctx.setVariable("confirmationLink", format("%s/confirmation/%s", hostName, userRegisteredEvent.getEmailConfirmationId()));
        ctx.setVariable("cancelConfirmationLink", format("%s/confirmationcancel/%s", hostName, userRegisteredEvent.getEmailConfirmationId()));
        ctx.setVariable("username", userRegisteredEvent.getUsername());
        ctx.setVariable("pinCode", userRegisteredEvent.getPinCode());

        String htmlLetter = templateEngine.process("emailConfirmation", ctx);
        sendEmail(userRegisteredEvent.getEmail(), "Подтверждение учетной записи", htmlLetter);
    }

    @Async
    @Override
    @EventListener
    public void sendLetterWithNewPinCode(@NonNull NewPinCodeRequestedEvent newPinCodeRequestedEvent) {
        log.debug("Send letter with new PinCode to: {}", newPinCodeRequestedEvent.getEmail());
        Context ctx = new Context();
        ctx.setVariable("username", newPinCodeRequestedEvent.getUsername());
        ctx.setVariable("pinCode", newPinCodeRequestedEvent.getPinCode());
        String letter = templateEngine.process("pinCodeRestore", ctx);
        sendEmail(newPinCodeRequestedEvent.getEmail(), "Восстановление пароля", letter);
    }

    @Override
    public void sendEmail(@NonNull String emailTo, @NonNull String subject, @NonNull String message) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            mimeMessageHelper.setTo(emailTo);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message, true);
            mimeMessageHelper.setFrom(emailFrom);
        } catch (MessagingException e) {
            log.error("Error while creating message to {}", emailTo);
            throw new RuntimeException(e);
        }
        log.info("Sending message to: {}", emailTo);
        emailSender.send(mimeMessage);
        log.info("message to {} was sent", emailTo);
    }
}
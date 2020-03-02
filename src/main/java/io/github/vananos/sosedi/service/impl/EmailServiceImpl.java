package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.models.EmailConfirmationInfo;
import io.github.vananos.sosedi.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    private final TaskExecutor taskExecutor;
    private final ITemplateEngine templateEngine;

    @Value("${email.from}")
    private String emailFrom;

    @Value("${sosedi.hostname}")
    private String hostName;


    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender, TaskExecutor taskExecutor, ITemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.taskExecutor = taskExecutor;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmailConfirmationLetter(EmailConfirmationInfo emailConfirmationInfo) {
        taskExecutor.execute(() -> {
            log.debug("Send confirmation email to {}", emailConfirmationInfo.getEmail());
            Context ctx = new Context();
            ctx.setVariable("confirmationLink", format("%s/confirmation/%s", hostName, emailConfirmationInfo.getEmailConfirmationId()));
            ctx.setVariable("cancelConfirmationLink", format("%s/confirmationcancel/%s", hostName, emailConfirmationInfo.getEmailConfirmationId()));
            ctx.setVariable("username", emailConfirmationInfo.getUsername());
            ctx.setVariable("pinCode", emailConfirmationInfo.getPinCode());

            String htmlLetter = templateEngine.process("emailConfirmation", ctx);
            sendEmail(emailConfirmationInfo.getEmail(), "Подтверждение учетной записи", htmlLetter);
        });
    }

    @Override
    public void sendEmail(String emailTo, String subject, String message) {
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
package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.models.events.NewPinCodeRequestedEvent;
import io.github.vananos.sosedi.models.events.UserRegisteredEvent;
import io.github.vananos.sosedi.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static io.github.vananos.sosedi.utils.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {
    private static final String SUBJECT = "subject";
    private static final String MESSAGE = "message";
    private static final String CONFIRMATION_ID = "confirmationId";

    @Mock
    private JavaMailSender mailSenderMock;

    @Mock
    private ITemplateEngine iTemplateEngineMock;

    private EmailService emailService;

    @BeforeEach
    public void setUp() {
        emailService = new EmailServiceImpl(mailSenderMock, iTemplateEngineMock, EMAIL_FROM, HOST_NAME);
        MimeMessage mimeMessageMock = new MimeMessage((Session) null);
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessageMock);
    }

    @Test
    void sendEmailConfirmationLetter() throws IOException, MessagingException {
        //given
        when(iTemplateEngineMock.process((String) any(), any())).thenReturn(MESSAGE);
        //when
        emailService.sendEmailConfirmationLetter(UserRegisteredEvent.builder()
                .email(VALID_EMAIL)
                .username(VALID_USERNAME)
                .emailConfirmationId(CONFIRMATION_ID)
                .pinCode(VALID_PINCODE)
                .build());
        //then
        verifyEmailWasSent(VALID_EMAIL, "Подтверждение учетной записи", MESSAGE);
    }

    @Test
    void sendLetterWithNewPinCode() throws IOException, MessagingException {
        //given
        when(iTemplateEngineMock.process((String) any(), any())).thenReturn(MESSAGE);
        //when
        emailService.sendLetterWithNewPinCode(NewPinCodeRequestedEvent.builder()
                .email(VALID_EMAIL)
                .username(VALID_USERNAME)
                .pinCode(VALID_PINCODE)
                .build());
        //then
        verifyEmailWasSent(VALID_EMAIL, "Восстановление пароля", MESSAGE);
    }

    @Test
    void sendEmail_shouldSendEmail() throws IOException, MessagingException {
        //when
        emailService.sendEmail(VALID_EMAIL, SUBJECT, MESSAGE);
        //then
        verifyEmailWasSent(VALID_EMAIL, SUBJECT, MESSAGE);
    }

    private void verifyEmailWasSent(String emailTo, String subject, String message) throws MessagingException, IOException {
        ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSenderMock).send(mimeMessageArgumentCaptor.capture());
        MimeMessage capturedMessage = mimeMessageArgumentCaptor.getValue();

        assertThat(capturedMessage.getAllRecipients()[0].toString()).isEqualTo(emailTo);
        assertThat(capturedMessage.getSubject()).isEqualTo(subject);
        assertThat(capturedMessage.getContent()).isEqualTo(message);

        verifyNoMoreInteractions(mailSenderMock);
    }
}
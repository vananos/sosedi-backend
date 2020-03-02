package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;

import static io.github.vananos.sosedi.utils.Constants.VALID_USERNAME;
import static io.github.vananos.sosedi.utils.UserBag.getUser;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailConfirmationControllerTest {
    private static final String EXISTING_CONFIRMATION_ID = "existingConfirmationId";
    private static final String NONEXISTENT_CONFIRMATION_ID = "nonExistentConfirmationId";

    @Mock
    private UserService userServiceMock;

    @Mock
    private HttpServletResponse httpServletResponseMock;

    private EmailConfirmationController emailConfirmationController;

    @BeforeEach
    public void setUp() {
        emailConfirmationController = new EmailConfirmationController(userServiceMock);
    }

    @Test
    public void confirmEmail_shouldRedirectToConfirmationHandlerWithConfirmedStatus_whenConfirmationWasSuccessful() throws IOException {
        //given
        when(userServiceMock.confirmEmail(EXISTING_CONFIRMATION_ID)).thenReturn(Optional.of(getUser()));
        //and
        String expectedRedirectPath = "/confirmationhandler?status=confirmed&username=" + URLEncoder.encode(VALID_USERNAME, UTF_8.toString());
        //when
        emailConfirmationController.confirmEmail(EXISTING_CONFIRMATION_ID, httpServletResponseMock);
        //then
        verify(httpServletResponseMock).sendRedirect(expectedRedirectPath);
    }

    @Test
    public void confirmEmail_shouldRedirectToConfirmationHandlerWithErrorStatus_whenConfirmationWasUnsuccessful() throws IOException {
        //given
        when(userServiceMock.confirmEmail(NONEXISTENT_CONFIRMATION_ID)).thenReturn(Optional.empty());
        //when
        emailConfirmationController.confirmEmail(NONEXISTENT_CONFIRMATION_ID, httpServletResponseMock);
        //then
        verify(httpServletResponseMock).sendRedirect("/confirmationhandler?status=error");
    }

    @Test
    public void cancelEmailConfirmation_shouldRedirectToConfirmationHandlerWithCancelledStatus_whenCancellationSucceed() throws IOException {
        //given
        when(userServiceMock.cancelConfirmation(EXISTING_CONFIRMATION_ID)).thenReturn(true);
        //when
        emailConfirmationController.canclerEmailConfirmation(EXISTING_CONFIRMATION_ID, httpServletResponseMock);
        //then
        verify(httpServletResponseMock).sendRedirect("/confirmationhandler?status=cancelled");
    }

    @Test
    public void cancelEmailConfirmation_shouldRedirectToConfirmationHandlerWithErrorStatus_whenCancellationWasUnsuccessful() throws IOException {
        //given
        when(userServiceMock.cancelConfirmation(NONEXISTENT_CONFIRMATION_ID)).thenReturn(false);
        //when
        emailConfirmationController.canclerEmailConfirmation(NONEXISTENT_CONFIRMATION_ID, httpServletResponseMock);
        //then
        verify(httpServletResponseMock).sendRedirect("/confirmationhandler?status=error");
    }
}
package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.vananos.sosedi.utils.Constants.VALID_EMAIL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class PinCodeRecoveryControllerTest {

    @Mock
    private UserService userService;

    private PinCodeRecoveryController pinCodeRecoveryController;

    @BeforeEach
    public void setUp() {
        pinCodeRecoveryController = new PinCodeRecoveryController(userService);
    }

    @Test
    public void requestNewPinCode_shouldCallUserServiceWhenNewPinCodeRequested() {
        //when
        pinCodeRecoveryController.requestNewPinCode(VALID_EMAIL);
        //then
        verify(userService).requestNewPinCodeFor(VALID_EMAIL);
        verifyNoMoreInteractions(userService);
    }
}

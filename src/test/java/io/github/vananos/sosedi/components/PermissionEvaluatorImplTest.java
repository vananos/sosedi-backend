package io.github.vananos.sosedi.components;


import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.security.UserDetailsImpl;
import io.github.vananos.sosedi.security.permission.PermissionEvaluatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PermissionEvaluatorImplTest {

    @Autowired
    private PermissionEvaluatorImpl permissionEvaluatorImpl;

    @Test
    public void shouldThrowExceptionForEmailUnconfirmedUsers() {
        User expectedUser = new User();
        expectedUser.setUserStatus(User.UserStatus.EMAIL_UNCONFIRMED);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(expectedUser));

        assertThrows(AccessDeniedException.class, () -> {
            permissionEvaluatorImpl.hasPermission(authentication, null, "any");
        });

    }
}
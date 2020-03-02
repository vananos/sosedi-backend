package io.github.vananos.sosedi.utils;

import io.github.vananos.sosedi.models.dto.registration.RegistrationRequest;

import static io.github.vananos.sosedi.utils.Constants.*;

public class RegistrationRequestBag {
    public static RegistrationRequest.Builder getRegistrationRequest() {
        return RegistrationRequest.builder()
                .name(VALID_USERNAME)
                .surname(VALID_SURNAME)
                .email(VALID_EMAIL);
    }
}

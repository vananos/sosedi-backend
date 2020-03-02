package io.github.vananos.sosedi.utils;

import io.github.vananos.sosedi.models.RegistrationInfo;

import static io.github.vananos.sosedi.utils.Constants.*;

public class RegistrationInfoBag {
    public static RegistrationInfo.Builder getRegistrationInfo() {
        return RegistrationInfo.builder()
                .name(VALID_USERNAME)
                .surname(VALID_SURNAME)
                .email(VALID_EMAIL);
    }
}

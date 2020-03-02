package io.github.vananos.sosedi.utils;

import io.github.vananos.sosedi.models.User;

import static io.github.vananos.sosedi.utils.Constants.*;
import static io.github.vananos.sosedi.utils.NotificationsBag.getNotifications;

public class UserBag {
    public static User getUser() {
        return new User()
                .setName(VALID_USERNAME)
                .setSurname(VALID_SURNAME)
                .setEmail(VALID_EMAIL)
                .setNotifications(getNotifications())
                .setPinCode(ENCODED_PINCODE);
    }
}

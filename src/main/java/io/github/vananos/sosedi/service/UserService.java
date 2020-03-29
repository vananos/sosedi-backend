package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.NotificationFrequency;
import io.github.vananos.sosedi.models.RegistrationInfo;
import io.github.vananos.sosedi.models.User;

import java.util.Optional;

public interface UserService {
    void registerUser(RegistrationInfo registrationInfo);

    User updateUserInfo(User user);

    User findUserById(long id);

    void setNotificationFrequencyForUser(long userId, NotificationFrequency notificationFrequency);

    void setAvatarForUser(String avatar, Long userId);

    void deleteAccount(Long userId);

    Optional<User> confirmEmail(String confirmationId);

    boolean cancelConfirmation(String confirmationId);

    boolean requestNewPinCodeFor(String email);
}

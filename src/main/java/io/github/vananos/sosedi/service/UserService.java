package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.RegistrationInfo;
import io.github.vananos.sosedi.models.User;

import java.util.Optional;

public interface UserService {
    void registerUser(RegistrationInfo registrationInfo);

    User updateUserInfo(User user);

    User findUserById(Long id);

    void setAvatarForUser(String avatar, Long userId);

    void updateUserPassword(Long userId, String password);

    void deleteAccount(Long userId);

    Optional<User> confirmEmail(String confirmationId);

    boolean cancelConfirmation(String confirmationId);
}

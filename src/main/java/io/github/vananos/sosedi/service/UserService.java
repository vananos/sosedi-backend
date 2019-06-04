package io.github.vananos.sosedi.service;

import io.github.vananos.sosedi.models.User;

public interface UserService {
    void registerUser(User user);

    User updateUserInfo(User user);

    User findUserById(Long id);

    void setAvatarForUser(String avatar, Long userId);
}

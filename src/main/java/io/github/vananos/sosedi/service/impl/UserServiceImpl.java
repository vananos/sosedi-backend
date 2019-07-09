package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.exceptions.UserAlreadyExistsException;
import io.github.vananos.sosedi.exceptions.UserNotFoundException;
import io.github.vananos.sosedi.models.NotificationFrequency;
import io.github.vananos.sosedi.models.Notifications;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.User.UserStatus;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.MatchService;
import io.github.vananos.sosedi.service.UserConfirmationService;
import io.github.vananos.sosedi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserConfirmationService userConfirmationService;
    private TaskExecutor taskExecutor;
    private MatchService matchService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, UserConfirmationService userConfirmationService,
                           TaskExecutor taskExecutor, MatchService matchService)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConfirmationService = userConfirmationService;
        this.taskExecutor = taskExecutor;
        this.matchService = matchService;
    }


    @Override
    public void registerUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmailConfirmationId(userConfirmationService.generateLink());
            Notifications notifications = new Notifications();
            notifications.setNotificationFrequency(NotificationFrequency.ONE_DAY);
            user.setNotifications(notifications);
            userRepository.save(user);
            taskExecutor.execute(() -> userConfirmationService.sendConfirmationLetter(user));
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException();
        }
    }


    @Override
    public User updateUserInfo(User user) {
        user = userRepository.save(user);
        if (user.getUserStatus() == UserStatus.AD_FILLED) {
            matchService.updateMatchesForUser(user);
        }
        return user;
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void setAvatarForUser(String avatar, Long userId) {
        userRepository.updateAvatarForUser(avatar, userId);
    }

    @Override
    public void updateUserPassword(Long userId, String password) {
        User user = findUserById(userId);
        user.setPassword(passwordEncoder.encode(password));
        updateUserInfo(user);
    }

    @Override
    public void deleteAccount(Long userId) {
        User user = findUserById(userId);
        user.setEmail(user.getEmail() + "_del_" + System.nanoTime());
        updateUserInfo(user);
    }
}
package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.exceptions.UserAlreadyExistsException;
import io.github.vananos.sosedi.exceptions.UserNotFoundException;
import io.github.vananos.sosedi.models.EmailConfirmationInfo;
import io.github.vananos.sosedi.models.Notifications;
import io.github.vananos.sosedi.models.RegistrationInfo;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.EmailService;
import io.github.vananos.sosedi.service.MatchService;
import io.github.vananos.sosedi.service.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.vananos.sosedi.models.NotificationFrequency.ONE_DAY;
import static io.github.vananos.sosedi.models.User.UserStatus.EMAIL_CONFIRMED;
import static io.github.vananos.sosedi.models.User.UserStatus.EMAIL_UNCONFIRMED;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MatchService matchService;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(@NonNull UserRepository userRepository,
                           @NonNull PasswordEncoder passwordEncoder,
                           @NonNull MatchService matchService,
                           @NonNull EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.matchService = matchService;
        this.emailService = emailService;
    }


    @Override
    public void registerUser(@NonNull RegistrationInfo registrationInfo) {
        Notifications notifications = new Notifications();
        notifications.setNotificationFrequency(ONE_DAY);

        String pinCode = String.format("%04d", ThreadLocalRandom.current().nextInt(0, 9999));
        String confirmationId = UUID.randomUUID().toString();

        User user = new User()
                .setName(registrationInfo.getName())
                .setSurname(registrationInfo.getSurname())
                .setPinCode(passwordEncoder.encode(pinCode))
                .setEmailConfirmationId(confirmationId)
                .setEmail(registrationInfo.getEmail())
                .setNotifications(notifications);

        EmailConfirmationInfo emailConfirmationInfo = EmailConfirmationInfo.builder()
                .username(registrationInfo.getName())
                .email(registrationInfo.getEmail())
                .emailConfirmationId(confirmationId)
                .pinCode(pinCode)
                .build();
        try {
            userRepository.save(user);
            log.info("User is registered: {}", registrationInfo);
            emailService.sendEmailConfirmationLetter(emailConfirmationInfo);
        } catch (DataIntegrityViolationException e) {
            log.debug("User with email: {} is already exists", registrationInfo.getEmail());
            throw new UserAlreadyExistsException();
        }
    }

    @Override
    public User updateUserInfo(User user) {
        user = userRepository.save(user);
        if (user.getUserStatus() == User.UserStatus.AD_FILLED) {
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
        user.setPinCode(passwordEncoder.encode(password));
        updateUserInfo(user);
    }

    @Override
    public void deleteAccount(Long userId) {
        User user = findUserById(userId);
        user.setEmail(user.getEmail() + "_del_" + System.nanoTime());
        updateUserInfo(user);
    }

    @Override
    public Optional<User> confirmEmail(@NonNull String confirmationId) {
        User user = userRepository.findByEmailConfirmationId(confirmationId);
        if (user == null) {
            log.debug("User with confirmationId: {} was not found", confirmationId);
            return Optional.empty();
        }
        user = user.setUserStatus(EMAIL_CONFIRMED);
        userRepository.save(user);
        log.info("Email confirmed for user id: {}", user.getId());

        return Optional.of(user);
    }

    @Override
    public boolean cancelConfirmation(@NonNull String confirmationId) {
        User user = userRepository.findByEmailConfirmationId(confirmationId);
        if (user == null) {
            log.debug("User with confirmationId: {} was not found", confirmationId);
            return false;
        }
        if (user.getUserStatus() != EMAIL_UNCONFIRMED) {
            log.debug("User id: {} unable to cancel confirmation with current status: {}", user.getId(), user.getUserStatus());
            return false;
        }
        userRepository.delete(user);
        log.info("User {} cancelled email confirmation", user);
        return true;
    }
}
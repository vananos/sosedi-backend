package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.exceptions.UserAlreadyExists;
import io.github.vananos.sosedi.exceptions.UserNotFoundException;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.UserConfirmationService;
import io.github.vananos.sosedi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserConfirmationService userConfirmationService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, UserConfirmationService userConfirmationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConfirmationService = userConfirmationService;
    }


    @Override
    public void registerUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmailConfirmationId(userConfirmationService.generateLink());
            userRepository.save(user);
            new Thread(() -> userConfirmationService.sendConfirmationLetter(user)).start();
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExists();
        }
    }


    @Override
    public User updateUserInfo(User newUser) {
        User oldUser = findUserById(newUser.getId());

        oldUser.setInterests(newUser.getInterests());
        oldUser.setUserStatus(newUser.getUserStatus());
        oldUser.setName(newUser.getName());
        oldUser.setSurname(newUser.getSurname());
        oldUser.setBirthday(newUser.getBirthday());
        oldUser.setPhone(newUser.getPhone());
        oldUser.setDescription(newUser.getDescription());

        return userRepository.save(oldUser);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void setAvatarForUser(String avatar, Long userId) {
        userRepository.updateAvatarForUser(avatar, userId);
    }
}
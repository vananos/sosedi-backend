package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordResetController {

    private UserRepository userRepository;

    private PasswordResetService passwordResetService;

    public PasswordResetController(UserRepository userRepository, PasswordResetService passwordResetService) {
        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/requestreset")
    public ResponseEntity requestPasswordRequest(@RequestParam("email") String email) {
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        passwordResetService.startPasswordResetForUser(user);
        return ResponseEntity.ok().build();
    }
}
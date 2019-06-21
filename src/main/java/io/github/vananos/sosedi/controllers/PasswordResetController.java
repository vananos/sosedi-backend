package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.passwordreset.PasswordResetRequest;
import io.github.vananos.sosedi.repository.UserRepository;
import io.github.vananos.sosedi.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static io.github.vananos.sosedi.components.validation.ErrorProcessingUtils.assertHasNoErrors;

@RestController
public class PasswordResetController {

    private UserRepository userRepository;

    private PasswordResetService passwordResetService;

    public PasswordResetController(UserRepository userRepository, PasswordResetService passwordResetService) {
        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/resetpassword")
    public void resetPassword(@Valid PasswordResetRequest passwordResetRequest, BindingResult bindingResult) {
        assertHasNoErrors(bindingResult);

        passwordResetService.resetPassword(passwordResetRequest.getSecret(), passwordResetRequest.getPassword());
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
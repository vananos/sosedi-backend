package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PinCodeRecoveryController {

    private final UserService userService;

    public PinCodeRecoveryController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/requestreset")
    public ResponseEntity requestNewPinCode(@RequestParam(value = "email") String email) {
        log.debug("New PinCode request for {}", email);
        userService.requestNewPinCodeFor(email);
        return ResponseEntity.ok().build();
    }
}
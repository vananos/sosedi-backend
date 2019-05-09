package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.register.RegistrationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class RegisterController {

    @PostMapping("/register")
    public String register(@RequestBody RegistrationRequest registrationRequest) throws IOException {
        System.out.println(registrationRequest);
        return null;
    }
}

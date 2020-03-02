package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.components.mappers.DTOMapper;
import io.github.vananos.sosedi.components.validation.RequestValidator;
import io.github.vananos.sosedi.models.RegistrationInfo;
import io.github.vananos.sosedi.models.dto.BaseResponse2;
import io.github.vananos.sosedi.models.dto.registration.RegistrationRequest;
import io.github.vananos.sosedi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
public class RegistrationController {
    public static final String USER_ALREADY_EXISTS = "user already exists";

    private final UserService userService;
    private final RequestValidator requestValidator;


    @Autowired
    public RegistrationController(UserService userService, RequestValidator requestValidator) {
        this.userService = userService;
        this.requestValidator = requestValidator;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse2> register(@RequestBody @Valid RegistrationRequest registrationRequest, BindingResult bindingResult) {
        requestValidator.assertValid(bindingResult);

        RegistrationInfo registrationInfo = DTOMapper.INSTANCE.toRegistrationInfo(registrationRequest);

        log.debug("Registration info received: {}", registrationInfo);
        userService.registerUser(registrationInfo);

        return ok(BaseResponse2.builder().build());
    }
}

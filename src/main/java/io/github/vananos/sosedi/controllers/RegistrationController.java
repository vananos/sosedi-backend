package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.components.validation.ErrorProcessor;
import io.github.vananos.sosedi.exceptions.UserAlreadyExists;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.models.dto.registration.Error;
import io.github.vananos.sosedi.models.dto.registration.RegistrationRequest;
import io.github.vananos.sosedi.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class RegistrationController {
    public static final String USER_ALREADY_EXISTS = "user already exists";
    public static final String PASSWORD_CONFIRMATION_MUST_MATCH = "password confirmation must match";

    private UserService userService;
    private ErrorProcessor errorProcessor;

    @Autowired
    public RegistrationController(UserService userService, ErrorProcessor errorProcessor)
    {
        this.userService = userService;
        this.errorProcessor = errorProcessor;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
            @RequestBody @Valid RegistrationRequest registrationRequest,
            BindingResult bindingResult)
    {
        Optional<List<Error>> errors = errorProcessor.handleErrors(bindingResult);
        if (errors.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponse().errors(errors.get()));
        }

        User user = new ModelMapper().map(registrationRequest, User.class);
        try {
            userService.registerUser(user);
        } catch (UserAlreadyExists e) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponse()
                            .errors(Collections.singletonList(
                                    new Error().description(USER_ALREADY_EXISTS))));
        }

        return ResponseEntity.ok(new BaseResponse());
    }
}

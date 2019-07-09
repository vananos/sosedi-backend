package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
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

import static io.github.vananos.sosedi.components.validation.ErrorProcessingUtils.assertHasNoErrors;

@RestController
public class RegistrationController {
    public static final String USER_ALREADY_EXISTS = "user already exists";
    public static final String PASSWORD_CONFIRMATION_MUST_MATCH = "password confirmation must match";

    private UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
            @RequestBody @Valid RegistrationRequest registrationRequest,
            BindingResult bindingResult)
    {

        assertHasNoErrors(bindingResult);

        User user = new ModelMapper().map(registrationRequest, User.class);
        userService.registerUser(user);

        return ResponseEntity.ok(new BaseResponse());
    }
}

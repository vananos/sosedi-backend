package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.components.converters.RegistrationRequestToUserConverter;
import io.github.vananos.sosedi.exceptions.UserAlreadyExists;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.models.dto.registration.Error;
import io.github.vananos.sosedi.models.dto.registration.RegistrationRequest;
import io.github.vananos.sosedi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class RegistrationController {
    public static final String USER_ALREADY_EXISTS = "user already exists";
    public static final String PASSWORD_CONFIRMATION_MUST_MATCH = "password confirmation must match";

    private RegistrationRequestToUserConverter registrationRequestToUserConverter;
    private UserService userService;

    @Autowired
    public RegistrationController(UserService userService, RegistrationRequestToUserConverter registrationRequestToUserConverter) {
        this.userService = userService;
        this.registrationRequestToUserConverter = registrationRequestToUserConverter;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
            @RequestBody @Valid RegistrationRequest registrationRequest,
            BindingResult bindingResult
    )
    {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (bindingResult.hasErrors()) {
            BaseResponse response = new BaseResponse();
            response.setStatus(BaseResponse.ResultStatus.FAIL);

            List<Error> errors = bindingResult.getFieldErrors().stream()
                    .map(e -> new Error().id(e.getField()).description(e.getDefaultMessage()))
                    .collect(toList());
            errors.addAll(bindingResult.getGlobalErrors().stream()
                    .map(e -> new Error().description(e.getDefaultMessage()))
                    .collect(toList()));

            response.setErrors(errors);
            return ResponseEntity.badRequest().body(response);
        }
        User user = registrationRequestToUserConverter.convert(registrationRequest);
        try {
            userService.registerUser(user);
        } catch (UserAlreadyExists e) {
            BaseResponse response = new BaseResponse();
            response.setStatus(BaseResponse.ResultStatus.FAIL);
            response.setErrors(Collections.singletonList(new Error().description(USER_ALREADY_EXISTS)));
            return ResponseEntity.badRequest().body(response);
        }
        BaseResponse response = new BaseResponse();
        response.setStatus(BaseResponse.ResultStatus.SUCCESS);
        return ResponseEntity.ok(response);
    }
}

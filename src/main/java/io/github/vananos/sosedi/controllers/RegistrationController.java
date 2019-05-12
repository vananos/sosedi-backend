package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.components.converters.RegistrationRequestToUserConverter;
import io.github.vananos.sosedi.exceptions.UserAlreadyExists;
import io.github.vananos.sosedi.models.BaseResponse;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.registration.RegistrationRequest;
import io.github.vananos.sosedi.models.registration.RegistrationResponse;
import io.github.vananos.sosedi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RegistrationController {
    public static final String USER_ALREADY_EXISTS = "user already exists";

    private RegistrationRequestToUserConverter registrationRequestToUserConverter;
    private UserService userService;

    @Autowired
    public RegistrationController(UserService userService, RegistrationRequestToUserConverter registrationRequestToUserConverter) {
        this.userService = userService;
        this.registrationRequestToUserConverter = registrationRequestToUserConverter;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @RequestBody @Valid RegistrationRequest registrationRequest,
            BindingResult bindingResult
    )
    {
        if (bindingResult.hasErrors()) {
            RegistrationResponse response = new RegistrationResponse();
            response.setResult(BaseResponse.ResultStatus.FAIL);

            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            response.setErrors(errors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        User user = registrationRequestToUserConverter.convert(registrationRequest);
        try {
            userService.registerUser(user);
        } catch (UserAlreadyExists e) {
            RegistrationResponse response = new RegistrationResponse();
            response.setResult(BaseResponse.ResultStatus.FAIL);
            response.setErrors(USER_ALREADY_EXISTS);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        RegistrationResponse response = new RegistrationResponse();
        response.setResult(BaseResponse.ResultStatus.SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

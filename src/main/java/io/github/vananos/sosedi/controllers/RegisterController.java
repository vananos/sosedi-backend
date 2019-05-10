package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.BaseResponse;
import io.github.vananos.sosedi.models.register.RegisterResponse;
import io.github.vananos.sosedi.models.register.RegistrationRequest;
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
public class RegisterController {

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegistrationRequest registrationRequest,
            BindingResult bindingResult
    )
    {
        if (bindingResult.hasErrors()) {
            RegisterResponse response = new RegisterResponse();
            response.setResult(BaseResponse.ResultStatus.FAIL);

            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            response.setErrors(errors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        System.out.println(registrationRequest);
        return null;
    }
}

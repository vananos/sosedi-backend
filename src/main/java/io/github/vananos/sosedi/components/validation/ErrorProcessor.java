package io.github.vananos.sosedi.components.validation;

import io.github.vananos.sosedi.models.dto.registration.Error;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Component
public class ErrorProcessor {

    public Optional<List<Error>> handleErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<Error> errors = bindingResult.getFieldErrors().stream()
                    .map(e -> new Error().id(e.getField()).description(e.getDefaultMessage()))
                    .collect(toList());
            errors.addAll(bindingResult.getGlobalErrors().stream()
                    .map(e -> new Error().description(e.getDefaultMessage()))
                    .collect(toList()));
            return Optional.of(errors);
        }
        return Optional.empty();
    }
}

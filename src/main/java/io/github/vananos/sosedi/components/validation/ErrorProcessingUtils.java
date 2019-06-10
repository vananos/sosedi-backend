package io.github.vananos.sosedi.components.validation;

import io.github.vananos.sosedi.exceptions.BadParametersException;
import io.github.vananos.sosedi.models.dto.registration.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ErrorProcessingUtils {

    public static void assertHasNoErrors(BindingResult bindingResult) {
        Optional<List<Error>> errors = handleErrors(bindingResult);
        if (errors.isPresent()) {
            throw new BadParametersException(errors.get());
        }
    }

    public static Optional<List<Error>> handleErrors(BindingResult bindingResult) {
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

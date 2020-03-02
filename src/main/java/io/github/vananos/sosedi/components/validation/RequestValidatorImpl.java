package io.github.vananos.sosedi.components.validation;

import io.github.vananos.sosedi.exceptions.BadParametersException;
import io.github.vananos.sosedi.models.dto.Error;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Component
public class RequestValidatorImpl implements RequestValidator {

    @Override
    public void assertValid(BindingResult bindingResult) {
        List<Error> errors = handleErrors(bindingResult);
        if (errors.isEmpty()) {
            return;
        }
        throw new BadParametersException(errors);
    }

    private List<Error> handleErrors(BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return emptyList();
        }
        List<Error> errors = bindingResult.getFieldErrors()
                .stream()
                .map(e -> new Error().id(e.getField()).description(e.getDefaultMessage()))
                .collect(toList());

        errors.addAll(bindingResult.getGlobalErrors()
                .stream()
                .map(e -> new Error().description(e.getDefaultMessage()))
                .collect(toList()));

        return errors;
    }
}
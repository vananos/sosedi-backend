package io.github.vananos.sosedi.components.validation;

import org.springframework.validation.BindingResult;

public interface RequestValidator {
    void assertValid(BindingResult bindingResult);
}

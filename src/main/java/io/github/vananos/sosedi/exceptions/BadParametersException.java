package io.github.vananos.sosedi.exceptions;

import io.github.vananos.sosedi.models.dto.registration.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadParametersException extends RuntimeException {
    private List<Error> errorList;

    public BadParametersException() {
    }

    public BadParametersException(List<Error> errorList) {
        this.errorList = errorList;
    }

    public BadParametersException(String message) {
        super(message);
    }

    public BadParametersException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadParametersException(Throwable cause) {
        super(cause);
    }

    public BadParametersException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public List<Error> getErrorList() {
        return errorList;
    }
}
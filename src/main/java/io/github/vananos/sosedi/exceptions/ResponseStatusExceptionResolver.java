package io.github.vananos.sosedi.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vananos.sosedi.models.dto.BaseResponse;
import io.github.vananos.sosedi.models.dto.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;

import static io.github.vananos.sosedi.controllers.RegistrationController.USER_ALREADY_EXISTS;

@Slf4j
@ControllerAdvice
public class ResponseStatusExceptionResolver extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadParametersException.class)
    public ResponseEntity<Object> handleBadRequest(final BadParametersException ex, final WebRequest request) throws JsonProcessingException {
        final String bodyOfResponse =
                new ObjectMapper().writeValueAsString(new BaseResponse<>().errors(ex.getErrorList()));
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleBadRequest(final EntityNotFoundException ex, final WebRequest request) throws JsonProcessingException {
        return handleExceptionInternal(ex, "entity not found", new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<BaseResponse> handleUserNotFound() {
        return ResponseEntity.badRequest()
                .body(new BaseResponse()
                        .errors(Collections.singletonList(
                                new Error().description(USER_ALREADY_EXISTS))));
    }
}
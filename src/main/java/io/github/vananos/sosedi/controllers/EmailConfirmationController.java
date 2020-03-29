package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
public class EmailConfirmationController {
    private final UserService userService;

    @Autowired
    public EmailConfirmationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/confirmation/{confirmationId}")
    public void confirmEmail(@PathVariable("confirmationId") String confirmationId, HttpServletResponse response) throws IOException {
        log.debug("Confirmation for confirmationId {} is requested", confirmationId);

        String confirmationPath = userService.confirmEmail(confirmationId)
                .map(user -> "?status=confirmed&username=" + user.getName())
                .orElse("?status=error");

        String redirectPath = "../confirmationhandler" + confirmationPath;
        response.sendRedirect(redirectPath);
    }

    @GetMapping("/confirmationcancel/{confirmationId}")
    public void canclerEmailConfirmation(@PathVariable("confirmationId") String confirmationId, HttpServletResponse response) throws IOException {
        log.debug("Cancellation for confirmationId {} is requested", confirmationId);

        boolean wasSuccessful = userService.cancelConfirmation(confirmationId);

        response.sendRedirect("../confirmationhandler?status=" + (wasSuccessful ? "cancelled" : "error"));
    }
}
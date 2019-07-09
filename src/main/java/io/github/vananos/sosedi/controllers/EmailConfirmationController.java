package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.service.UserConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
public class EmailConfirmationController {

    public UserConfirmationService userConfirmationService;

    @Autowired
    public EmailConfirmationController(UserConfirmationService userConfirmationService) {
        this.userConfirmationService = userConfirmationService;
    }

    @GetMapping("/confirmation/{confirmationId}")
    public void confirmEmail(@PathVariable("confirmationId") String confirmationId, HttpServletResponse response) throws IOException {
        Optional<User> user = userConfirmationService.confirmEmail(confirmationId);
        String redirectPath = "/confirmhandler" + (user.isPresent() ?
                "?status=confirmed&username=" + URLEncoder.encode(user.get().getName(),
                        StandardCharsets.UTF_8.toString()
                )
                : "?status=error");

        response.sendRedirect(redirectPath);
    }

    @GetMapping("/confirmationcancel/{confirmationId}")
    public void canclerEmailConfirmation(@PathVariable("confirmationId") String confirmationId,
                                         HttpServletResponse response) throws IOException
    {

        boolean wasSuccessfull = userConfirmationService.cancelConfirmation(confirmationId);

        response.sendRedirect("/confirmhandler?status=" + (wasSuccessfull ? "cancelled" : "error"));
    }
}
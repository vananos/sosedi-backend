package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.settings.ChangeNotificationSettingsRequest;
import io.github.vananos.sosedi.models.dto.settings.ChangePasswordRequest;
import io.github.vananos.sosedi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static io.github.vananos.sosedi.components.validation.ErrorProcessingUtils.assertHasNoErrors;

@RestController
public class SettingsController {

    private UserService userService;

    @Autowired
    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/changepassword")
    @PreAuthorize("hasPermission(#changePasswordRequest, 'write')")
    public ResponseEntity changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                         BindingResult bindingResult)
    {
        assertHasNoErrors(bindingResult);

        userService.updateUserPassword(changePasswordRequest.getUserId(), changePasswordRequest.getPassword());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/changenotifications")
    @PreAuthorize("hasPermission(#changeNotificationSettingsRequest, 'write')")
    public ResponseEntity changeNotificationSettings(@RequestBody @Valid ChangeNotificationSettingsRequest changeNotificationSettingsRequest, BindingResult bindingResult) {
        assertHasNoErrors(bindingResult);

        User user = userService.findUserById(changeNotificationSettingsRequest.getUserId());
        user.getNotifications().setNotificationFrequency(changeNotificationSettingsRequest.getNotificationFrequency());

        userService.updateUserInfo(user);

        return ResponseEntity.ok().build();
    }
}
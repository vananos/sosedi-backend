package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.BaseResponse;
import io.github.vananos.sosedi.models.dto.settings.ChangeNotificationSettingsRequest;
import io.github.vananos.sosedi.models.dto.settings.ChangePasswordRequest;
import io.github.vananos.sosedi.models.dto.settings.UserSettingsResponse;
import io.github.vananos.sosedi.security.UserDetailsImpl;
import io.github.vananos.sosedi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static io.github.vananos.sosedi.components.validation.ErrorProcessingUtils.assertHasNoErrors;

@RestController
@RequestMapping("/settings")
public class SettingsController {

    private UserService userService;
    private SessionRegistry sessionRegistry;

    @Autowired
    public SettingsController(UserService userService, SessionRegistry sessionRegistry) {
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
    }

    @RequestMapping(value = "/pincode", method = RequestMethod.POST)
    @PreAuthorize("hasPermission(#changePasswordRequest, 'write')")
    public ResponseEntity changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                         BindingResult bindingResult)
    {
        assertHasNoErrors(bindingResult);

        userService.updateUserPassword(changePasswordRequest.getUserId(), changePasswordRequest.getPassword());

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/notifications", method = RequestMethod.POST)
    @PreAuthorize("hasPermission(#changeNotificationSettingsRequest, 'write')")
    public ResponseEntity changeNotificationSettings(@RequestBody @Valid ChangeNotificationSettingsRequest changeNotificationSettingsRequest, BindingResult bindingResult) {
        assertHasNoErrors(bindingResult);

        User user = userService.findUserById(changeNotificationSettingsRequest.getUserId());
        user.getNotifications().setNotificationFrequency(changeNotificationSettingsRequest.getNotificationFrequency());

        userService.updateUserInfo(user);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasPermission(#userId, 'UserSettings', 'read')")
    public ResponseEntity<BaseResponse<UserSettingsResponse>> getUserSettings(@RequestParam("userid") Long userId) {
        UserSettingsResponse userSettingsResponse = new UserSettingsResponse();
        userSettingsResponse.setNotificationFrequency(userService.findUserById(userId).getNotifications().getNotificationFrequency());

        return ResponseEntity.ok(new BaseResponse<UserSettingsResponse>().data(userSettingsResponse));
    }

    @RequestMapping(value = "/deleteaccount", method = RequestMethod.POST)
    @PreAuthorize("hasPermission(#userId, 'User', 'delete')")
    public ResponseEntity deleteAccount(@RequestParam("userid") Long userId, HttpServletRequest request,
                                        Authentication authentication)
    {
        userService.deleteAccount(userId);

        sessionRegistry.getAllPrincipals()
                .stream()
                .filter(p -> ((UserDetailsImpl) p).getUser().getId().equals(userId))
                .forEach(p -> sessionRegistry.getAllSessions(p, false)
                        .forEach(SessionInformation::expireNow)
                );

        return ResponseEntity.ok().build();
    }
}
package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.User.UserStatus;
import io.github.vananos.sosedi.models.dto.BaseResponse;
import io.github.vananos.sosedi.models.dto.userprofile.UserProfileInfo;
import io.github.vananos.sosedi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.github.vananos.sosedi.components.validation.ErrorProcessingUtils.assertHasNoErrors;

@RestController
@Slf4j
public class UserProfileController {
    private UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasPermission(#userId, 'UserProfile', 'read')")
    public ResponseEntity<BaseResponse<UserProfileInfo>> getUserProfileInfo(@RequestParam("userid") Long userId) {
        User user = userService.findUserById(userId);

        UserProfileInfo userProfileInfo = new ModelMapper().map(user, UserProfileInfo.class);

        userProfileInfo.setIsNewUser(user.getUserStatus() == UserStatus.EMAIL_CONFIRMED);

        return ResponseEntity.ok(new BaseResponse().data(userProfileInfo));
    }

    @PostMapping("/profile")
    @PreAuthorize("hasPermission(#userProfileInfo, 'write')")
    public ResponseEntity<BaseResponse> updateUserProfile(
            @RequestBody @Valid UserProfileInfo userProfileInfo,
            BindingResult bindingResult) {

        assertHasNoErrors(bindingResult);
        User user = userService.findUserById(userProfileInfo.getId());
        new ModelMapper().addMappings(new PropertyMap<UserProfileInfo, User>() {
            protected void configure() {
                skip().setAvatar(null);
            }
        }).map(userProfileInfo, user);
        if (user.getUserStatus() == UserStatus.EMAIL_CONFIRMED) {
            user.setUserStatus(User.UserStatus.PROFILE_FILLED);
        }
        userService.updateUserInfo(user);
        return ResponseEntity.ok(new BaseResponse());
    }
}

package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.components.validation.ErrorProcessor;
import io.github.vananos.sosedi.exceptions.UserNotFoundException;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.models.dto.registration.Error;
import io.github.vananos.sosedi.models.dto.userprofile.UserProfileInfo;
import io.github.vananos.sosedi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

@RestController
@Slf4j
public class UserProfileController {
    private ErrorProcessor errorProcessor;
    private UserService userService;
    private ModelMapper modelMapper;

    public UserProfileController(UserService userService, ErrorProcessor errorProcessor) {
        this.errorProcessor = errorProcessor;
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasPermission(#userId, 'UserProfile', 'read')")
    public ResponseEntity<BaseResponse<UserProfileInfo>> getUserProfileInfo(@RequestParam("userid") Long userId) {
        User user;
        user = userService.findUserById(userId);

        UserProfileInfo userProfileInfo = new ModelMapper().map(user, UserProfileInfo.class);

        userProfileInfo.setInterests(
                isNull(user.getInterests()) ?
                        Collections.emptyList() :
                        asList(user.getInterests().split(",")));

        userProfileInfo.setIsNewUser(user.getUserStatus() != User.UserStatus.PROFILE_FILLED);

        return ResponseEntity.ok(new BaseResponse().data(userProfileInfo));
    }

    @PostMapping("/profile")
    @PreAuthorize("hasPermission(#userProfileInfo, 'write')")
    public ResponseEntity<BaseResponse> updateUserProfile(
            @RequestBody @Valid UserProfileInfo userProfileInfo,
            BindingResult bindingResult) {
        Optional<List<Error>> errors = errorProcessor.handleErrors(bindingResult);
        if (errors.isPresent()) {
            return ResponseEntity.badRequest().body(new BaseResponse().errors(errors.get()));
        }
        User user = new ModelMapper().map(userProfileInfo, User.class);
        user.setInterests(String.join(",", userProfileInfo.getInterests()));
        user.setUserStatus(User.UserStatus.PROFILE_FILLED);
        userService.updateUserInfo(user);
        return ResponseEntity.ok(new BaseResponse());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity userNotFoundExceptionHandler() {
        return ResponseEntity.notFound().build();
    }
}

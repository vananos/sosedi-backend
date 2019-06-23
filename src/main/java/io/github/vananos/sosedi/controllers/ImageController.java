package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.service.FileService;
import io.github.vananos.sosedi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Optional;

@RestController
@Slf4j
public class ImageController {

    private FileService fileService;

    private UserService userService;

    public ImageController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/photo")
    @PreAuthorize("hasPermission(#request, 'savePhoto')")
    public ResponseEntity<BaseResponse> savePhoto(MultipartHttpServletRequest request) {

        MultipartFile file = request.getFile("file");
        Long userId = Long.parseLong(request.getParameter("userId"));

        BufferedImage image = tryDeserializeImage(file)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));


        String fileName = fileService.saveFile(image);
        userService.setAvatarForUser(fileName, userId);

        return ResponseEntity.ok(new BaseResponse().data(Collections.singletonMap("name", fileName)));
    }

    private Optional<BufferedImage> tryDeserializeImage(MultipartFile file) {
        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) {
                return Optional.empty();
            }
        } catch (Throwable e) {
            return Optional.empty();
        }
        return Optional.of(image);
    }

    @PostMapping("/deleteavatar")
    @PreAuthorize("hasPermission(#userId, 'deleteAvatar')")
    public ResponseEntity<BaseResponse> deleteAvatar(@RequestParam("userid") Long userId) {
        User user = userService.findUserById(userId);
        if (user.getAvatar() != null) {
            fileService.deleteFile(user.getAvatar());
            user.setAvatar(null);
            userService.updateUserInfo(user);
        }
        return ResponseEntity.ok().build();
    }
}

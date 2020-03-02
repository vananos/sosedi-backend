package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.models.dto.BaseResponse;
import io.github.vananos.sosedi.service.FileService;
import io.github.vananos.sosedi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Optional;

@RestController
@Slf4j
public class AvatarController {

    private FileService fileService;

    private UserService userService;

    @Autowired
    public AvatarController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/avatar")
    @PreAuthorize("hasPermission(#userId, 'saveAvatar')")
    public ResponseEntity<BaseResponse> saveAvatar(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("userId") Long userId)
    {
        BufferedImage image = tryDeserializeImage(file)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));


        String fileName = fileService.saveFile(image);
        userService.setAvatarForUser(fileName, userId);

        return ResponseEntity.ok(new BaseResponse().data(Collections.singletonMap("name", fileName)));
    }

    private Optional<BufferedImage> tryDeserializeImage(MultipartFile file) {
        BufferedImage image;
        try (ByteArrayInputStream bs = new ByteArrayInputStream(file.getBytes())) {
            image = ImageIO.read(bs);
            if (image == null) {
                return Optional.empty();
            }
        } catch (Throwable e) {
            return Optional.empty();
        }
        return Optional.of(image);
    }

    @DeleteMapping("/avatar")
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

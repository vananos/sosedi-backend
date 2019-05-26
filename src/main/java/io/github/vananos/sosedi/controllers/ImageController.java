package io.github.vananos.sosedi.controllers;

import io.github.vananos.sosedi.models.dto.registration.BaseResponse;
import io.github.vananos.sosedi.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

@RestController
@Slf4j
public class ImageController {

    private FileService fileService;

    public ImageController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/photo")
    public ResponseEntity<BaseResponse> savePhoto(MultipartHttpServletRequest request) {

        MultipartFile file = request.getFile("file");

        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) {
                throw new IOException("not an image");
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }

        String fileName = fileService.saveFile(image);

        return ResponseEntity.ok(new BaseResponse().data(Collections.singletonMap("name", fileName)));
    }

    @DeleteMapping("/photo")
    public ResponseEntity<BaseResponse> deletePhoto(@RequestParam("name") String photoName) {
        fileService.deleteFile(photoName);
        return ResponseEntity.ok().build();
    }
}

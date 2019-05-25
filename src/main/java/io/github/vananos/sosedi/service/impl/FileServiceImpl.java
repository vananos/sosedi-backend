package io.github.vananos.sosedi.service.impl;

import io.github.vananos.sosedi.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private Path folderPath;

    public FileServiceImpl(@Value("${sosedi.images.folderPath}") String folderPath) {
        this.folderPath = Paths.get(folderPath);
    }

    @Override
    public String saveFile(byte[] bytes, String extension) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String saveFile(BufferedImage image) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        File outputFile = folderPath.resolve(fileName).toFile();

        if (outputFile.exists()) {
            throw new RuntimeException("Same random filename found ^_^: " + outputFile.toString());
        }

        try {
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileName;
    }

    @Override
    public void deleteFile(String name) {
        checkFileName(name);
        try {
            Files.delete(folderPath.resolve(name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkFileName(String name) {
        int extensionIndex = name.lastIndexOf(".");
        if (extensionIndex == -1) {
            throw new RuntimeException("filename must contain extension: " + name);
        }
        String baseName = name.substring(0, extensionIndex);
        if (baseName.contains(".")) {
            throw new RuntimeException("basename must not contain dots: " + name);
        }
        File file = folderPath.resolve(name).toFile();

        if (!file.exists()) {
            throw new RuntimeException("file does not exist: " + name);
        }
    }
}

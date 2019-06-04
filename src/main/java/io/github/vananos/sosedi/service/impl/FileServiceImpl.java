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
        String fileName = UUID.randomUUID().toString() + ".png";
        File outputFile = folderPath.resolve(fileName).toFile();

        if (outputFile.exists()) {
            throw new RuntimeException("Same random filename found ^_^: " + outputFile.toString());
        }

        try {
            ImageIO.write(image, "png", outputFile);
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
        if (name.lastIndexOf(".") == -1) {
            throw new RuntimeException("filename must contain extension: " + name);
        }
        String baseName = new File(name).getName(); // prevent ../

        File file = folderPath.resolve(baseName).toFile();

        if (!file.exists()) {
            throw new RuntimeException("file does not exist: " + name);
        }
    }
}

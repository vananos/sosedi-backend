package io.github.vananos.sosedi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Value("${sosedi.images.folderPath}")
    private String imageFolder;

    @Test
    public void saveFile_success() throws IOException {
        String fileName = saveImage();
        assertThat(new File(imageFolder + "/" + fileName)).exists();
    }


    @Test
    public void deleteFile_success() throws IOException {
        String fileName = saveImage();
        fileService.deleteFile(fileName);
        assertThat(new File(imageFolder + "/" + fileName)).doesNotExist();
    }

    private String saveImage() throws IOException {
        BufferedImage image =
                ImageIO.read(new File(getClass().getClassLoader().getResource("imgtest/sun.png").getPath()));
        return fileService.saveFile(image);
    }
}

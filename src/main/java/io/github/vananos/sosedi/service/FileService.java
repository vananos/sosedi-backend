package io.github.vananos.sosedi.service;

import java.awt.image.BufferedImage;

public interface FileService {
    String saveFile(byte[] bytes, String extension);

    String saveFile(BufferedImage image);

    void deleteFile(String name);
}

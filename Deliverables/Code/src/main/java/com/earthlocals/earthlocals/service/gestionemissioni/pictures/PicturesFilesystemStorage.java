package com.earthlocals.earthlocals.service.gestionemissioni.pictures;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class PicturesFilesystemStorage implements PicturesStorageService {
    @Value("${earthlocalsfiles.pictures}")
    private Path tempDir;

    @Override
    public String acceptUpload(MultipartFile file) throws IOException {
        String newName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Path target = tempDir.resolve(newName).normalize();
        if (!target.startsWith(tempDir)) {
            throw new IllegalArgumentException("File outside of target directory");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target);
        }

        return newName;
    }

    @Override
    public InputStream downloadFile(String fileName) throws IOException {
        var path = tempDir.resolve(fileName);
        return Files.newInputStream(path);
    }
}

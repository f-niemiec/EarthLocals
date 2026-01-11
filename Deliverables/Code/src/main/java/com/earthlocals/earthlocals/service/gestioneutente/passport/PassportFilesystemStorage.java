package com.earthlocals.earthlocals.service.gestioneutente.passport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class PassportFilesystemStorage implements PassportStorageService {

    @Value("${earthlocalsfiles.passport}")
    private Path tempDir;


    @Override
    public String acceptUpload(MultipartFile file) throws IOException {
        var newName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        var target = getPathPassaport(newName);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target);
        }

        return newName;
    }

    @Override
    public InputStream downloadFile(String fileName) throws IOException {
        var path = getPathPassaport(fileName);
        return Files.newInputStream(path);
    }

    @Override
    public FileSystemResource downloadFileResource(String fileName) {
        var path = getPathPassaport(fileName);
        return new FileSystemResource(path);
    }

    private Path getPathPassaport(String fileName) {
        var target = tempDir.resolve(fileName).normalize();
        if (!target.startsWith(tempDir)) {
            throw new IllegalArgumentException("File outside of target directory");
        }
        return target;
    }

    @Override
    public boolean removeFile(String fileName) {
        Path path = tempDir.resolve(fileName);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }


}

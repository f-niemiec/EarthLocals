package com.earthlocals.earthlocals.service.gestioneutente.passport;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Transactional
public class PassportFilesystemStorage implements PassportStorageService {

    @Value("${earthlocalsfiles.passport}")
    private Path tempDir;


    @Override
    public String acceptUpload(MultipartFile file) throws IOException {
        var newName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        var target = getPathPassaport(newName);

        file.transferTo(target);

        return newName;
    }


    @Override
    public FileSystemResource downloadFile(String fileName) {
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

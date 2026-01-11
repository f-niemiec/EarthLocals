package com.earthlocals.earthlocals.service.gestionemissioni.pictures;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Transactional
public class PicturesFilesystemStorage implements PicturesStorageService {
    @Value("${earthlocalsfiles.pictures}")
    private Path tempDir;

    @Override
    public String acceptUpload(MultipartFile file) throws IOException {
        String newName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        var target = getPathPicture(newName);
        file.transferTo(target);

        return newName;
    }

    private Path getPathPicture(String fileName) {
        var target = tempDir.resolve(fileName).normalize();
        if (!target.startsWith(tempDir)) {
            throw new IllegalArgumentException("File outside of target directory");
        }
        return target;
    }

    @Override
    public FileSystemResource downloadFile(String fileName) throws IOException {
        var path = getPathPicture(fileName);
        return new FileSystemResource(path);
    }
}

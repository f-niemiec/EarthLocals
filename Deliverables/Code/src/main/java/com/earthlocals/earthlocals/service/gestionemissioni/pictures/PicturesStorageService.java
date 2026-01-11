package com.earthlocals.earthlocals.service.gestionemissioni.pictures;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

public interface PicturesStorageService {
    String acceptUpload(MultipartFile file) throws Exception;

    FileSystemResource downloadFile(String fileName) throws Exception;
}

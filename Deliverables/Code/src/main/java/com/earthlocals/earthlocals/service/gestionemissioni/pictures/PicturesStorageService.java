package com.earthlocals.earthlocals.service.gestionemissioni.pictures;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface PicturesStorageService {
    String acceptUpload(MultipartFile file) throws Exception;

    Resource downloadFile(String fileName) throws Exception;
}

package com.earthlocals.earthlocals.service.gestionemissioni.pictures;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface PicturesStorageService {
    public String acceptUpload(MultipartFile file) throws Exception;

    public InputStream downloadFile(String fileName) throws Exception;
}

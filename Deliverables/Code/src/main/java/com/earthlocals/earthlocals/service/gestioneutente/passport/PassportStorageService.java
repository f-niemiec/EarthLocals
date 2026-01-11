package com.earthlocals.earthlocals.service.gestioneutente.passport;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface PassportStorageService {

    public String acceptUpload(MultipartFile file) throws Exception;

    public InputStream downloadFile(String fileName) throws Exception;

    public FileSystemResource downloadFileResource(String fileName);

    boolean removeFile(String fileName);
}

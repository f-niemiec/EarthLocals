package com.earthlocals.earthlocals.service.gestioneutente.passport;

import jakarta.transaction.Transactional;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

@Transactional
public interface PassportStorageService {

    String acceptUpload(MultipartFile file) throws Exception;

    FileSystemResource downloadFile(String fileName);

    boolean removeFile(String fileName);
}

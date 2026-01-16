package com.earthlocals.earthlocals.service.gestioneutente.passport;

import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

@Transactional
public interface PassportStorageService {

    String acceptUpload(MultipartFile file) throws Exception;

    Resource downloadFile(String fileName);

    boolean removeFile(String fileName);
}

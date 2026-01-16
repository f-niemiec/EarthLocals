package com.earthlocals.earthlocals.service.gestioneutente.passport;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Primary
public class PassportS3Storage implements PassportStorageService {

    private final static String BUCKET_NAME = "earthlocals-passports";

    private final S3Template s3Template;


    @Override
    public String acceptUpload(MultipartFile file) throws Exception {
        var newName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        s3Template.upload(BUCKET_NAME, newName, file.getInputStream());
        return newName;
    }

    @Override
    public Resource downloadFile(String fileName) {
        return s3Template.download(BUCKET_NAME, fileName);
    }

    @Override
    public boolean removeFile(String fileName) {
        s3Template.deleteObject(BUCKET_NAME, fileName);
        return true;
    }
}

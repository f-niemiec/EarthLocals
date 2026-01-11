package com.earthlocals.earthlocals.utility.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

public class FileTypeValidator
        implements ConstraintValidator<FileType, Object> {

    @Autowired
    private Tika tika;

    private FileType fileTypeAnnotation;


    @Override
    public void initialize(FileType constraintAnnotation) {
        this.fileTypeAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        var file = (MultipartFile) obj;
        if (file.isEmpty()) {
            return false;
        }
        try {
            var fileType = tika.detect(file.getInputStream());
            if (Arrays.stream(fileTypeAnnotation.allowedExtensions()).anyMatch(fileType::startsWith)) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}

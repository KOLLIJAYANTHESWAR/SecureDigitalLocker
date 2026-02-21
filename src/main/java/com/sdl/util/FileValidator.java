package com.sdl.util;

import com.sdl.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public final class FileValidator {

    private FileValidator() {}

    private static final Set<String> ALLOWED_DOCUMENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    private static final long MAX_DOCUMENT_SIZE = 50 * 1024 * 1024; // 50MB
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

    public static void validateDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File is empty or not provided");
        }
        if (!ALLOWED_DOCUMENT_TYPES.contains(file.getContentType())) {
            throw new BusinessException("File type not allowed: " + file.getContentType());
        }
        if (file.getSize() > MAX_DOCUMENT_SIZE) {
            throw new BusinessException("File size exceeds maximum limit of 50MB");
        }
    }

    public static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Image file is empty or not provided");
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new BusinessException("Image type not allowed: " + file.getContentType());
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException("Image size exceeds maximum limit of 5MB");
        }
    }
}

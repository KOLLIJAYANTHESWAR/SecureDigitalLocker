package com.sdl.serviceImpl;

import com.sdl.config.StorageConfig;
import com.sdl.exception.BusinessException;
import com.sdl.service.StorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final StorageConfig storageConfig;

    public LocalStorageService(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(storageConfig.getLocal().getUploadDir()));
            Files.createDirectories(Paths.get(storageConfig.getLocal().getProfileDir()));
            Files.createDirectories(Paths.get(storageConfig.getLocal().getDocumentDir()));
        } catch (IOException e) {
            throw new BusinessException("Could not initialize storage directories: " + e.getMessage());
        }
    }

    @Override
    public String upload(MultipartFile file, String subDirectory) {
        try {
            String baseDir = storageConfig.getLocal().getUploadDir();
            Path uploadPath = Paths.get(baseDir, subDirectory);
            Files.createDirectories(uploadPath);

            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String uniqueFileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(uniqueFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new BusinessException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    public Resource download(String storagePath) {
        try {
            Path filePath = Paths.get(storagePath);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new BusinessException("File not found or not readable: " + storagePath);
            }
        } catch (MalformedURLException e) {
            throw new BusinessException("Invalid file path: " + storagePath);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            Path filePath = Paths.get(storagePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new BusinessException("Failed to delete file: " + e.getMessage());
        }
    }
}

package com.sdl.serviceImpl;

import com.sdl.exception.BusinessException;
import com.sdl.service.StorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * GCP Cloud Storage implementation.
 * This is a placeholder that can be activated by setting storage.type=gcp
 * and adding the Google Cloud Storage dependency.
 *
 * To enable:
 * 1. Add google-cloud-storage dependency to pom.xml
 * 2. Set storage.type=gcp in application.properties
 * 3. Configure GCP credentials
 * 4. Implement the methods below
 */
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "gcp")
public class GcpStorageService implements StorageService {

    @Override
    public String upload(MultipartFile file, String subDirectory) {
        // TODO: Implement GCP Cloud Storage upload
        // Storage storage = StorageOptions.getDefaultInstance().getService();
        // BlobId blobId = BlobId.of(bucketName, subDirectory + "/" + uniqueFileName);
        // BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        // storage.create(blobInfo, file.getBytes());
        throw new BusinessException("GCP Storage not yet configured. Please implement GcpStorageService.");
    }

    @Override
    public Resource download(String storagePath) {
        // TODO: Implement GCP Cloud Storage download
        throw new BusinessException("GCP Storage not yet configured. Please implement GcpStorageService.");
    }

    @Override
    public void delete(String storagePath) {
        // TODO: Implement GCP Cloud Storage delete
        throw new BusinessException("GCP Storage not yet configured. Please implement GcpStorageService.");
    }
}

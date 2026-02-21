package com.sdl.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String upload(MultipartFile file, String subDirectory);

    Resource download(String storagePath);

    void delete(String storagePath);
}
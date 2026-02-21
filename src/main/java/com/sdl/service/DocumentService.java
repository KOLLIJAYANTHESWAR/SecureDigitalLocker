package com.sdl.service;

import com.sdl.dto.request.UploadDocumentRequestDTO;
import com.sdl.dto.response.DocumentResponseDTO;
import com.sdl.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    DocumentResponseDTO uploadDocument(MultipartFile file,
                                       UploadDocumentRequestDTO visibilityRequest,
                                       User uploader);

    Resource downloadDocument(Long documentId, User requester);

    void deleteDocument(Long documentId, User requester);

    List<DocumentResponseDTO> getMyDocuments(User user);

    List<DocumentResponseDTO> getVisibleDocuments(User user);

    DocumentResponseDTO getDocumentInfo(Long documentId, User requester);

    boolean canAccess(Long documentId, User user);
}
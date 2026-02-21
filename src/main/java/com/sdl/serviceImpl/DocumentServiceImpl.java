package com.sdl.serviceImpl;

import com.sdl.dto.request.UploadDocumentRequestDTO;
import com.sdl.dto.response.DocumentResponseDTO;
import com.sdl.entity.AuditLog;
import com.sdl.entity.Document;
import com.sdl.entity.DocumentVisibility;
import com.sdl.entity.User;
import com.sdl.exception.AccessDeniedException;
import com.sdl.exception.ResourceNotFoundException;
import com.sdl.repository.DocumentRepository;
import com.sdl.repository.DocumentVisibilityRepository;
import com.sdl.service.*;
import com.sdl.util.FileValidator;
import com.sdl.util.MapperUtil;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

        private final DocumentRepository documentRepository;
        private final DocumentVisibilityRepository visibilityRepository;
        private final StorageService storageService;
        private final VisibilityService visibilityService;
        private final AuditService auditService;

        public DocumentServiceImpl(DocumentRepository documentRepository,
                        DocumentVisibilityRepository visibilityRepository,
                        StorageService storageService,
                        VisibilityService visibilityService,
                        AuditService auditService) {
                this.documentRepository = documentRepository;
                this.visibilityRepository = visibilityRepository;
                this.storageService = storageService;
                this.visibilityService = visibilityService;
                this.auditService = auditService;
        }

        @Override
        @Transactional
        public DocumentResponseDTO uploadDocument(MultipartFile file, UploadDocumentRequestDTO visibilityRequest,
                        User uploader) {
                FileValidator.validateDocument(file);

                String storagePath = storageService.upload(file, "documents");

                Document document = Document.builder()
                                .fileName(file.getOriginalFilename())
                                .storagePath(storagePath)
                                .contentType(file.getContentType())
                                .fileSize(file.getSize())
                                .uploadedBy(uploader)
                                .build();

                document = documentRepository.save(document);

                if (visibilityRequest != null) {
                        visibilityService.setDocumentVisibility(
                                        document,
                                        visibilityRequest.getVisibleToUserIds(),
                                        visibilityRequest.getVisibleToRoles());
                }

                auditService.log(AuditLog.ActionType.UPLOAD, uploader, "Document", document.getId(),
                                "Uploaded document: " + file.getOriginalFilename());

                List<DocumentVisibility> visibilities = visibilityRepository.findByDocument_Id(document.getId());
                return MapperUtil.toDocumentResponse(document, visibilities);
        }

        @Override
        public Resource downloadDocument(Long documentId, User requester) {
                Document document = documentRepository.findById(documentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

                if (!visibilityService.canUserAccessDocument(requester, document)) {
                        throw new AccessDeniedException("You do not have access to this document");
                }

                auditService.log(AuditLog.ActionType.DOWNLOAD, requester, "Document", documentId,
                                "Downloaded document: " + document.getFileName());

                return storageService.download(document.getStoragePath());
        }

        @Override
        @Transactional
        public void deleteDocument(Long documentId, User requester) {
                Document document = documentRepository.findById(documentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

                // Only uploader or higher roles can delete
                if (!document.getUploadedBy().getId().equals(requester.getId())
                                && !com.sdl.security.RoleHierarchy.isStrictlyHigher(requester.getRole(),
                                                document.getUploadedBy().getRole())) {
                        throw new AccessDeniedException("You cannot delete this document");
                }

                storageService.delete(document.getStoragePath());
                visibilityService.removeDocumentVisibility(documentId);
                documentRepository.delete(document);

                auditService.log(AuditLog.ActionType.DELETE, requester, "Document", documentId,
                                "Deleted document: " + document.getFileName());
        }

        @Override
        public List<DocumentResponseDTO> getMyDocuments(User user) {
                return documentRepository.findByUploadedByIdOrderByUploadedAtDesc(user.getId()).stream()
                                .map(doc -> {
                                        List<DocumentVisibility> vis = visibilityRepository
                                                        .findByDocument_Id(doc.getId());
                                        return MapperUtil.toDocumentResponse(doc, vis);
                                })
                                .collect(Collectors.toList());
        }

        @Override
        public List<DocumentResponseDTO> getVisibleDocuments(User user) {
                List<DocumentVisibility> visibilities = visibilityRepository
                                .findByVisibleToUser_IdOrVisibleToRole(user.getId(), user.getRole());

                // Also include own documents
                List<Document> ownDocs = documentRepository.findByUploadedById(user.getId());

                List<Document> visibleDocs = visibilities.stream()
                                .map(DocumentVisibility::getDocument)
                                .distinct()
                                .collect(Collectors.toList());

                // Merge and deduplicate
                ownDocs.stream()
                                .filter(doc -> visibleDocs.stream().noneMatch(vd -> vd.getId().equals(doc.getId())))
                                .forEach(visibleDocs::add);

                return visibleDocs.stream()
                                .map(doc -> {
                                        List<DocumentVisibility> vis = visibilityRepository
                                                        .findByDocument_Id(doc.getId());
                                        return MapperUtil.toDocumentResponse(doc, vis);
                                })
                                .collect(Collectors.toList());
        }

        @Override
        public DocumentResponseDTO getDocumentInfo(Long documentId, User requester) {
                Document document = documentRepository.findById(documentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

                if (!visibilityService.canUserAccessDocument(requester, document)) {
                        throw new AccessDeniedException("You do not have access to this document");
                }

                List<DocumentVisibility> visibilities = visibilityRepository.findByDocument_Id(documentId);
                return MapperUtil.toDocumentResponse(document, visibilities);
        }

        @Override
        public boolean canAccess(Long documentId, User user) {
                Document document = documentRepository.findById(documentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));
                return visibilityService.canUserAccessDocument(user, document);
        }
}

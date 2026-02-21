package com.sdl.controller;

import com.sdl.dto.request.UploadDocumentRequestDTO;
import com.sdl.dto.response.ApiResponseDTO;
import com.sdl.dto.response.DocumentResponseDTO;
import com.sdl.entity.User;
import com.sdl.exception.AccessDeniedException;
import com.sdl.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/documents", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // Upload document
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO<DocumentResponseDTO>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "visibleToUserIds", required = false) List<Long> visibleToUserIds,
            @RequestParam(value = "visibleToRoles", required = false) List<String> visibleToRoles,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        UploadDocumentRequestDTO visibilityRequest = new UploadDocumentRequestDTO();
        visibilityRequest.setVisibleToUserIds(visibleToUserIds);
        visibilityRequest.setVisibleToRoles(visibleToRoles);

        DocumentResponseDTO response =
                documentService.uploadDocument(file, visibilityRequest, user);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Document uploaded successfully", response)
        );
    }

    // Download document
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long documentId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        Resource resource =
                documentService.downloadDocument(documentId, user);

        DocumentResponseDTO docInfo =
                documentService.getDocumentInfo(documentId, user);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + docInfo.getFileName() + "\"")
                .body(resource);
    }

    // Delete document
    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponseDTO<String>> deleteDocument(
            @PathVariable Long documentId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        documentService.deleteDocument(documentId, user);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Document deleted successfully")
        );
    }

    // Get my uploaded documents
    @GetMapping("/my-documents")
    public ResponseEntity<ApiResponseDTO<List<DocumentResponseDTO>>> getMyDocuments(
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        List<DocumentResponseDTO> documents =
                documentService.getMyDocuments(user);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Your documents fetched", documents)
        );
    }

    // Get all visible documents
    @GetMapping("/visible")
    public ResponseEntity<ApiResponseDTO<List<DocumentResponseDTO>>> getVisibleDocuments(
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        List<DocumentResponseDTO> documents =
                documentService.getVisibleDocuments(user);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Visible documents fetched", documents)
        );
    }

    // Get document info
    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponseDTO<DocumentResponseDTO>> getDocumentInfo(
            @PathVariable Long documentId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        DocumentResponseDTO response =
                documentService.getDocumentInfo(documentId, user);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Document info fetched", response)
        );
    }
}
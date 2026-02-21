package com.sdl.service;

import com.sdl.entity.Document;
import com.sdl.entity.User;

import java.util.List;

public interface VisibilityService {

    boolean canUserAccessDocument(User user, Document document);

    void setDocumentVisibility(Document document,
            List<Long> userIds,
            List<String> roles);

    void removeDocumentVisibility(Long documentId);
}
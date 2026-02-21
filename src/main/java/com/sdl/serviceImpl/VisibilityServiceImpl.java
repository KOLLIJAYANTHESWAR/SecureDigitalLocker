package com.sdl.serviceImpl;

import com.sdl.entity.Document;
import com.sdl.entity.DocumentVisibility;
import com.sdl.entity.User;
import com.sdl.repository.DocumentVisibilityRepository;
import com.sdl.repository.UserRepository;
import com.sdl.security.RoleHierarchy;
import com.sdl.service.VisibilityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VisibilityServiceImpl implements VisibilityService {

    private final DocumentVisibilityRepository visibilityRepository;
    private final UserRepository userRepository;

    public VisibilityServiceImpl(DocumentVisibilityRepository visibilityRepository, UserRepository userRepository) {
        this.visibilityRepository = visibilityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean canUserAccessDocument(User user, Document document) {
        // Owner can always access
        if (document.getUploadedBy().getId().equals(user.getId())) {
            return true;
        }

        // Admin can access everything
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }

        // Higher roles can access documents from lower roles
        if (RoleHierarchy.isStrictlyHigher(user.getRole(), document.getUploadedBy().getRole())) {
            return true;
        }

        // Check explicit visibility
        List<DocumentVisibility> visibilities = visibilityRepository
                .findAccessForUser(document.getId(), user.getId(), user.getRole());

        return !visibilities.isEmpty();
    }

    @Override
    @Transactional
    public void setDocumentVisibility(Document document, List<Long> userIds, List<String> roles) {
        if (userIds != null) {
            for (Long userId : userIds) {
                User targetUser = userRepository.findById(userId).orElse(null);
                if (targetUser != null) {
                    DocumentVisibility visibility = DocumentVisibility.builder()
                            .document(document)
                            .visibleToUser(targetUser)
                            .build();
                    visibilityRepository.save(visibility);
                }
            }
        }

        if (roles != null) {
            for (String role : roles) {
                try {
                    User.Role targetRole = User.Role.valueOf(role.toUpperCase());
                    DocumentVisibility visibility = DocumentVisibility.builder()
                            .document(document)
                            .visibleToRole(targetRole)
                            .build();
                    visibilityRepository.save(visibility);
                } catch (IllegalArgumentException ignored) {
                    // Skip invalid roles
                }
            }
        }
    }

    @Override
    @Transactional
    public void removeDocumentVisibility(Long documentId) {
        visibilityRepository.deleteByDocument_Id(documentId);
    }
}

package com.sdl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document_visibility", indexes = {
        @Index(name = "idx_visibility_document", columnList = "document_id"),
        @Index(name = "idx_visibility_user", columnList = "visible_to_user_id"),
        @Index(name = "idx_visibility_role", columnList = "visible_to_role")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_document_user_visibility", columnNames = { "document_id", "visible_to_user_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVisibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visible_to_user_id")
    private User visibleToUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "visible_to_role")
    private User.Role visibleToRole;

    @PrePersist
    @PreUpdate
    private void validateVisibility() {
        if (visibleToUser == null && visibleToRole == null) {
            throw new IllegalStateException(
                    "Either visibleToUser or visibleToRole must be provided.");
        }
    }
}
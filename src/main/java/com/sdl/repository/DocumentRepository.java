package com.sdl.repository;

import com.sdl.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUploadedById(Long userId);

    List<Document> findByUploadedByIdOrderByUploadedAtDesc(Long userId);

    List<Document> findAllByOrderByUploadedAtDesc();
}
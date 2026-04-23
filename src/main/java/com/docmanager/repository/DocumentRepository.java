package com.docmanager.repository;

import com.docmanager.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findAllByDeletedFalse(Pageable pageable);

    Optional<Document> findByIdAndDeletedFalse(Long id);

    @Query("SELECT d FROM Document d WHERE d.deleted = false " +
           "AND (:name IS NULL OR LOWER(d.originalName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:startDate IS NULL OR d.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR d.createdAt <= :endDate) " +
           "AND (:category IS NULL OR d.category = :category)")
    Page<Document> search(
            @Param("name") String name,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("category") String category,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(d.fileSize), 0) FROM Document d WHERE d.deleted = false")
    Long getTotalStorageUsed();

    long countByDeletedFalse();
}

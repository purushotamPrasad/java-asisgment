package com.qssence.backend.document_initiation_service.repository;

import com.qssence.backend.document_initiation_service.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    @Query("SELECT t FROM Template t WHERE t.documentType.id = :documentTypeId " +
           "AND (:subTypeId IS NULL AND t.subType IS NULL OR (t.subType IS NOT NULL AND t.subType.id = :subTypeId)) " +
           "AND (:classificationId IS NULL AND t.classification IS NULL OR (t.classification IS NOT NULL AND t.classification.id = :classificationId))")
    Optional<Template> findByDocumentTypeAndSubTypeAndClassification(
            @Param("documentTypeId") Long documentTypeId,
            @Param("subTypeId") Long subTypeId,
            @Param("classificationId") Long classificationId);
}

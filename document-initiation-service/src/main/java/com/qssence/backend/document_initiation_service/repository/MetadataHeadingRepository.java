package com.qssence.backend.document_initiation_service.repository;

import com.qssence.backend.document_initiation_service.model.MetadataHeading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataHeadingRepository extends JpaRepository<MetadataHeading, Long> {
    
    @Query("SELECT mh FROM MetadataHeading mh WHERE mh.documentType.id = :documentTypeId AND mh.subType.id = :subTypeId AND mh.classification.id = :classificationId ORDER BY mh.displayOrder")
    List<MetadataHeading> findByDocumentTypeAndSubTypeAndClassification(
            @Param("documentTypeId") Long documentTypeId,
            @Param("subTypeId") Long subTypeId,
            @Param("classificationId") Long classificationId);
    
    @Query("SELECT mh FROM MetadataHeading mh WHERE mh.documentType.id = :documentTypeId ORDER BY mh.displayOrder")
    List<MetadataHeading> findByDocumentTypeId(@Param("documentTypeId") Long documentTypeId);
    
    // Method for client panel service
    List<MetadataHeading> findByDocumentTypeIdAndSubTypeIdAndClassificationId(
            Long documentTypeId, Long subTypeId, Long classificationId);
    
    // Check if heading already exists for same combination
    @Query("SELECT mh FROM MetadataHeading mh WHERE mh.documentType.id = :documentTypeId AND mh.subType.id = :subTypeId AND mh.classification.id = :classificationId AND mh.headingName = :headingName")
    MetadataHeading findByDocumentTypeAndSubTypeAndClassificationAndHeadingName(
            @Param("documentTypeId") Long documentTypeId,
            @Param("subTypeId") Long subTypeId,
            @Param("classificationId") Long classificationId,
            @Param("headingName") String headingName);
} 
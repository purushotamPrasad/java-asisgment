package com.qssence.backend.document_initiation_service.repository;

import com.qssence.backend.document_initiation_service.model.MetadataField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataFieldRepository extends JpaRepository<MetadataField, Long> {
    
    @Query("SELECT mf FROM MetadataField mf WHERE mf.metadataHeading.id = :headingId ORDER BY mf.displayOrder")
    List<MetadataField> findByMetadataHeadingIdOrderByDisplayOrder(@Param("headingId") Long headingId);
    
    @Query("SELECT mf FROM MetadataField mf WHERE mf.metadataHeading.documentType.id = :documentTypeId ORDER BY mf.metadataHeading.displayOrder, mf.displayOrder")
    List<MetadataField> findByDocumentTypeIdOrderByHeadingAndFieldOrder(@Param("documentTypeId") Long documentTypeId);
}

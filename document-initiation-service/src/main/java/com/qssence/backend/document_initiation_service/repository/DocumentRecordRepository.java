package com.qssence.backend.document_initiation_service.repository;

import com.qssence.backend.document_initiation_service.model.document.DocumentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRecordRepository extends JpaRepository<DocumentRecord, Long> {
    List<DocumentRecord> findByCreatedBy(Long createdBy);
    List<DocumentRecord> findByStatus(String status);
    List<DocumentRecord> findByDocumentTypeIdAndSubTypeIdAndClassificationId(
            Long documentTypeId, Long subTypeId, Long classificationId);
}

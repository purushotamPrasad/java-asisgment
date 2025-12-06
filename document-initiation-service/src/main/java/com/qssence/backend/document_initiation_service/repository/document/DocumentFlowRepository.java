package com.qssence.backend.document_initiation_service.repository.document;

import com.qssence.backend.document_initiation_service.model.document.DocumentFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentFlowRepository extends JpaRepository<DocumentFlow, Long> {
    
    // Find DocumentFlow by document type, subtype, and classification combination
    Optional<DocumentFlow> findByDocumentTypeIdAndSubTypeIdAndClassificationId(
            Long documentTypeId, Long subTypeId, Long classificationId);
}

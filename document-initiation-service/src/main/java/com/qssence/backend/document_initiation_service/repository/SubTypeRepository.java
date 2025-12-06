package com.qssence.backend.document_initiation_service.repository;

import com.qssence.backend.document_initiation_service.model.SubType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubTypeRepository extends JpaRepository<SubType, Long> {
    List<SubType> findByDocumentTypeId(Long documentTypeId);
}

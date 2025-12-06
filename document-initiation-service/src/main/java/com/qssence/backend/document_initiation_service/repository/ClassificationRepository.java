package com.qssence.backend.document_initiation_service.repository;

import com.qssence.backend.document_initiation_service.model.Classification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassificationRepository extends JpaRepository<Classification, Long> {
    List<Classification> findBySubTypeId(Long subTypeId);
}

package com.qssence.backend.document_initiation_service.repository;

import com.qssence.backend.document_initiation_service.model.DocumentType;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    @Query("select d from DocumentType d where d.id = :id")
    Optional<DocumentType> findById(@Param("id") Long id);
}

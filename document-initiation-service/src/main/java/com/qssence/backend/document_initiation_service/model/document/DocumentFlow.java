package com.qssence.backend.document_initiation_service.model.document;

import com.qssence.backend.document_initiation_service.dto.UserMasterDto;
import com.qssence.backend.document_initiation_service.model.Classification;
import com.qssence.backend.document_initiation_service.model.DocumentType;
import com.qssence.backend.document_initiation_service.model.SubType;
import com.qssence.backend.document_initiation_service.model.workflow.Workflow;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to DocumentType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    // Reference to SubType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_type_id", nullable = false)
    private SubType subType;

    // Reference to Classification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classification_id", nullable = false)
    private Classification classification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    // Query members (user IDs from Auth Service)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> queryMemberIds;

    // For response only: full user details fetched via Auth service
    @Transient
    private List<UserMasterDto> queryMembers;

}

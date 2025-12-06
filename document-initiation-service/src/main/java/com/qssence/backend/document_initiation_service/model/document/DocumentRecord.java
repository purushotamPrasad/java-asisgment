package com.qssence.backend.document_initiation_service.model.document;

import com.qssence.backend.document_initiation_service.model.Classification;
import com.qssence.backend.document_initiation_service.model.DocumentType;
import com.qssence.backend.document_initiation_service.model.SubType;
import com.qssence.backend.document_initiation_service.model.workflow.Workflow;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String documentName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_type_id", nullable = false)
    private SubType subType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classification_id", nullable = false)
    private Classification classification;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;
    
    @Column(name = "current_state")
    private String currentState;
    
    @Column(name = "template_path")
    private String templatePath;
    
    @Column(name = "metadata_values", columnDefinition = "TEXT")
    private String metadataValues; // JSON string of field values
    
    @Column(name = "created_by")
    private Long createdBy; // User ID from auth service
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "status")
    private String status; // DRAFT, IN_PROGRESS, COMPLETED, etc.
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "DRAFT";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

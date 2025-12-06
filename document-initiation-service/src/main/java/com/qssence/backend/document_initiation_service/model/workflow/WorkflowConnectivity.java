package com.qssence.backend.document_initiation_service.model.workflow;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowConnectivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @OneToMany(mappedBy = "workflowConnectivity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowStateConnectivity> states;

    // workflow  revision date flag
    // workflow revision date
    private LocalDate revisionDate;

}
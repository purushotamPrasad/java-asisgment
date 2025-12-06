package com.qssence.backend.document_initiation_service.model.workflow;

import com.qssence.backend.document_initiation_service.enums.WorkflowType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String workflowName;

    @Enumerated(EnumType.STRING)
    private WorkflowType workflowType; // GLOBAL, REGION, LOCAL

    private String region;   // For REGION and LOCAL workflow
    private String country;  // For LOCAL workflow
    private Long plantId;    // For LOCAL workflow (comes from auth-service)

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowState> states;

}

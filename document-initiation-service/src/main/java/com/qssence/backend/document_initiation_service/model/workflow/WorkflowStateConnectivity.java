package com.qssence.backend.document_initiation_service.model.workflow;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowStateConnectivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to state
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private WorkflowState state;

    // Link back to workflow connectivity
    @ManyToOne
    @JoinColumn(name = "workflow_connectivity_id", nullable = false)
    private WorkflowConnectivity workflowConnectivity;

    // Electronic Signature (Yes/No)
    private Boolean electronicSignature;

    @OneToMany(mappedBy = "stateConnectivity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProcessActivity> processActivities;

    @OneToMany(mappedBy = "stateConnectivity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<WorkflowComplete> workflowCompletes;




}

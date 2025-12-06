package com.qssence.backend.document_initiation_service.model.workflow;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowComplete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String completeName;

    // Each workflow complete can also have multiple custom fields
    @OneToMany(mappedBy = "workflowComplete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DynamicDataField> fields;

    @ManyToOne
    @JoinColumn(name = "state_connectivity_id")
    private WorkflowStateConnectivity stateConnectivity;
}


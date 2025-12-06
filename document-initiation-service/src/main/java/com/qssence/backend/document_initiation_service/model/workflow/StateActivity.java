package com.qssence.backend.document_initiation_service.model.workflow;

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
public class StateActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String activityName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id") // ✅ foreign key
    private WorkflowState state;

    @ManyToOne
    @JoinColumn(name = "state_connectivity_id")
    private WorkflowStateConnectivity stateConnectivity;

    // Link to process activities (optional, if you want mapping)
    @OneToMany(mappedBy = "stateActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessActivity> processActivities;
}

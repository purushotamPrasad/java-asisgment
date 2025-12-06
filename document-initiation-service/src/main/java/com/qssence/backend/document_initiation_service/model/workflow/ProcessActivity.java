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
public class ProcessActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String activityName;

    // Each activity can have multiple custom fields
    @OneToMany(mappedBy = "processActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DynamicDataField> fields;

    @ManyToOne
    @JoinColumn(name = "state_connectivity_id")
    private WorkflowStateConnectivity stateConnectivity;

    @ManyToOne
    @JoinColumn(name = "state_activity_id") // link process activity to state activity
    private StateActivity stateActivity;


}

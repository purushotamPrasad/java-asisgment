package com.qssence.backend.document_initiation_service.model.workflow;

import com.qssence.backend.document_initiation_service.enums.FieldType;
import com.qssence.backend.document_initiation_service.enums.FieldWidth;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DynamicDataField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fieldName;

    @Enumerated(EnumType.STRING)
    private FieldType fieldType;

    @Enumerated(EnumType.STRING)
    private FieldWidth width;

    private Long minLength;
    private Long maxLength;

    private String selectLine;

    @ElementCollection
    private List<String> options;

    // Relation with ProcessActivity
    @ManyToOne
    @JoinColumn(name = "process_activity_id")
    private ProcessActivity processActivity;

    // Relation with WorkflowComplete
    @ManyToOne
    @JoinColumn(name = "workflow_complete_id")
    private WorkflowComplete workflowComplete;

    @ManyToOne
    @JoinColumn(name = "state_activity_id") // optional: dynamic field for specific state activity
    private StateActivity stateActivity;
}

package com.qssence.backend.document_initiation_service.model.workflow;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"branches", "child", "activities", "workflow"})
@ToString(exclude = {"branches", "child", "activities", "workflow"})
public class WorkflowState {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stateName;
    private Integer orderNumber;

    @OneToMany(mappedBy = "workflowState", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Branch> branches;

    @OneToMany(mappedBy = "workflowState", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Child> child;

    private Long groupId; // from auth-service - stores the selected group ID
    private String groupName; // from auth-service - stores the selected group name

    @ElementCollection
    private List<Long> memberIds; // from auth-service

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StateActivity> activities;   // ✅ now proper entity relationship

    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

}

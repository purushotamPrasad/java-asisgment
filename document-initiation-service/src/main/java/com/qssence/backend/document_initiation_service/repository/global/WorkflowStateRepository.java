package com.qssence.backend.document_initiation_service.repository.global;

import com.qssence.backend.document_initiation_service.model.workflow.WorkflowState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowStateRepository extends JpaRepository<WorkflowState, Long> {

    List<WorkflowState> findByWorkflowId(Long workflowId);

}

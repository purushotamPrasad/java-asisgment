package com.qssence.backend.document_initiation_service.repository.global;

import com.qssence.backend.document_initiation_service.model.workflow.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    boolean existsByWorkflowName(String workflowName);

}

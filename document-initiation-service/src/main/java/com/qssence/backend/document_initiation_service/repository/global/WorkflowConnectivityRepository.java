package com.qssence.backend.document_initiation_service.repository.global;

import com.qssence.backend.document_initiation_service.model.workflow.WorkflowConnectivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowConnectivityRepository extends JpaRepository<WorkflowConnectivity, Long> {


    Optional<WorkflowConnectivity> findByWorkflow_Id(Long workflowId);
    void deleteByWorkflow_Id(Long workflowId);

}

package com.qssence.backend.subscription_panel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignPlanRequest {

    @NotNull(message = "Company ID is required")
    private String  companyId;  // Added to associate the plan with a specific company

//    private Long planId;
//    private List<Long> featureIds;  // Optional, but can be validated if needed

    private List<PlanFeatureDto> plans; // 🟢 Multiple plan-feature pairs

}

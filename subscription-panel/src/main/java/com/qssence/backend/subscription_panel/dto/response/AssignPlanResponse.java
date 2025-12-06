package com.qssence.backend.subscription_panel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignPlanResponse {

    private String id;
    private String companyName;
    private String location;
    private String email;
//    private PlanResponse plan;
private List<PlanResponse> plans; // 🟢 Convert single plan → List of plans

}

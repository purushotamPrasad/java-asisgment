package com.qssence.backend.subscription_panel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanResponse {

    private Long planId;
    private String name;
    private String description;
    private List<FeatureResponse> features;

}

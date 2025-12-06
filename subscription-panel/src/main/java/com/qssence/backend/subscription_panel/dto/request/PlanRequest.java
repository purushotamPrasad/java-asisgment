package com.qssence.backend.subscription_panel.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanRequest {

    private String name;
    private String description;
    private List<FeatureRequest> features;
}

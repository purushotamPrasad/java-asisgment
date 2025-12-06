package com.qssence.backend.subscription_panel.dto.response;

import com.qssence.backend.subscription_panel.dbo.plans.Feature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeatureResponse {
    private Long featuresId;
    private String name;

    public FeatureResponse(Long featuresId, String name, Feature feature) {
    }
}


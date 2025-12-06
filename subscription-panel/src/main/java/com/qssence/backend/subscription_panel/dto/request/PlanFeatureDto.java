package com.qssence.backend.subscription_panel.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PlanFeatureDto {

    private Long planId;
    private List<Long> featureIds;
}

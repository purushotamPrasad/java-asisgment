package com.qssence.backend.authservice.dto.Key;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlanDto {
    private Long planId;
    private String name;
    private String description;
    private List<FeatureDto> features;
}
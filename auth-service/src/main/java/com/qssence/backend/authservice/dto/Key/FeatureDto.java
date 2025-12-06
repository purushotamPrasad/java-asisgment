package com.qssence.backend.authservice.dto.Key;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeatureDto {
    private Long featuresId;
    private String name;
}
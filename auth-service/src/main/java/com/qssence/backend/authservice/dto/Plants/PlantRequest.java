package com.qssence.backend.authservice.dto.Plants;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlantRequest {
    private String plantName;
    private String region;
    private String country;
    private String location;
    private List<DepartmentRequest> department;
}

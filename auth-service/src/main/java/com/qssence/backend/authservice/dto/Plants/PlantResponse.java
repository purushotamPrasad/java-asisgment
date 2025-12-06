package com.qssence.backend.authservice.dto.Plants;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlantResponse {
    private Long id;
    private String plantName;
    private String region;
    private String country;
    private String location;
    private List<DepartmentResponse> department;
//    private CompanyResponse companyResponse;
}

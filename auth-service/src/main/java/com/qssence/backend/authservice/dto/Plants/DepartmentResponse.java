package com.qssence.backend.authservice.dto.Plants;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DepartmentResponse {
    private Long departmentId;
    private String departmentName;
    private List<SectionResponse> section;
}

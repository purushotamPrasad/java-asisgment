package com.qssence.backend.authservice.dto.Plants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentRequest {
    private long departmentId;
    private String departmentName;
    private List<SectionRequest> section;
}

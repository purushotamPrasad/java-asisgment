package com.qssence.backend.document_initiation_service.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PlantResponse {
    private Long id;
    private String plantName;
    private String region;
    private String country;
    private String location;
    private List<DepartmentResponse> department;

    @Data
    public static class DepartmentResponse {
        private Long departmentId;
        private String departmentName;
        private List<SectionResponse> section;
    }

    @Data
    public static class SectionResponse {
        private Long id;
        private String sectionName;
    }
}

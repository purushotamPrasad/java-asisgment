package com.qssence.backend.document_initiation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String userFirstName;
    private String userMiddleName;
    private String userLastName;
    private String userEmailId;
    private String employeeId;
    private String status;
    private String designation;
} 
package com.qssence.backend.audittrailservice.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class APIError {
    private int error_code;
    private String error_name;
    private String error_description;
}

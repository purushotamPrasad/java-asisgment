package com.qssence.backend.audittrailservice.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuccessStatus <T>{
    private boolean success;
    private String message;
    private T data;
}


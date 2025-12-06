package com.qssence.backend.authservice.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetData {
    private String token;
    private String resetLink;
}
package com.qssence.backend.authservice.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordResponse {

    private boolean success;
    private String message;
    private String resetLink; // 👈 Token ke sath pura reset link return karenge

}

package com.qssence.backend.authservice.controller;

import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.request.ForgotPasswordRequest;
import com.qssence.backend.authservice.dto.request.ResetPasswordRequest;
import com.qssence.backend.authservice.dto.responce.ForgotPasswordResponse;
import com.qssence.backend.authservice.dto.responce.ResetData;
import com.qssence.backend.authservice.service.MailServices.SendMail;
import com.qssence.backend.authservice.service.implementation.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final SendMail sendMail;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            ResetData resetData = passwordResetService.createPasswordResetToken(request.getEmail());
            sendMail.sendForgotPasswordEmail(request.getEmail(), resetData.getResetLink(), resetData.getToken());

            ForgotPasswordResponse response = new ForgotPasswordResponse(
                    true,
                    "Reset password link sent to your email!",
                    resetData.getResetLink()
            );

            return ResponseEntity.ok(
                    ApiResponse.<ForgotPasswordResponse>builder()
                            .success(true)
                            .message("Reset link generated successfully.")
                            .data(response)
                            .build()
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<ForgotPasswordResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ApiResponse.<ForgotPasswordResponse>builder()
                            .success(false)
                            .message("Failed to send reset password email.")
                            .data(null)
                            .build()
            );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Void>builder()
                            .success(false)
                            .message("Passwords do not match!")
                            .data(null)
                            .build()
            );
        }

        String result = passwordResetService.resetPassword(
                request.getToken(),
                request.getEmail(),
                request.getNewPassword()
        );

        if ("Password reset successfully!".equals(result)) {
            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(true)
                            .message(result)
                            .data(null)
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Void>builder()
                            .success(false)
                            .message(result)
                            .data(null)
                            .build()
            );
        }
    }
}

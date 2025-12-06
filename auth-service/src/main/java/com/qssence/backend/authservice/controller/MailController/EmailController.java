package com.qssence.backend.authservice.controller.MailController;

import com.qssence.backend.authservice.entity.SmtpConfig;
import com.qssence.backend.authservice.service.MailServices.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.qssence.backend.authservice.dto.ApiResponse;


import java.util.List;

@RestController
@RequestMapping("/api/v1/mail")
public class EmailController {

    @Autowired
    private MailService emailService;

    // Add new mail configuration
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<SmtpConfig>> saveMailConfig(@RequestBody SmtpConfig smtpConfig) {
        emailService.SaveMailConfig(smtpConfig);
        ApiResponse<SmtpConfig> response = ApiResponse.<SmtpConfig>builder()
                .success(true)
                .message("Mail configuration saved successfully.")
                .data(smtpConfig)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all mail configurations
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<SmtpConfig>>> getSmtpConfigList() {
        List<SmtpConfig> smtpConfigs = emailService.ShowAllConfig();
        ApiResponse<List<SmtpConfig>> response = ApiResponse.<List<SmtpConfig>>builder()
                .success(true)
                .message("Mail configurations retrieved successfully.")
                .data(smtpConfigs)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // Delete mail configuration by ID
    @DeleteMapping("/config/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMailConfig(@PathVariable Long id) {
        boolean isDeleted = emailService.deleteEmailConfig(id);
        if (isDeleted) {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(true)
                    .message("Mail configuration deleted successfully.")
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(false)
                    .message("Mail configuration not found.")
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Update mail configuration by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<SmtpConfig>> updateMailConfig(
            @PathVariable Long id, @RequestBody SmtpConfig updatedConfig) {
        try {
            emailService.updateMailConfig(id, updatedConfig);
            ApiResponse<SmtpConfig> response = ApiResponse.<SmtpConfig>builder()
                    .success(true)
                    .message("Mail configuration updated successfully.")
                    .data(updatedConfig)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            ApiResponse<SmtpConfig> response = ApiResponse.<SmtpConfig>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


//    //mail send api
//    @PostMapping("/send")
//    public String sendEmail(@RequestParam String providerName, @RequestParam String to,
//                            @RequestParam String subject, @RequestParam String body,
//                            @RequestParam boolean isHtml) {
//        emailService.sendEmail(providerName, to, subject, body, isHtml);
//        return "Email sent successfully!";
//    }
}

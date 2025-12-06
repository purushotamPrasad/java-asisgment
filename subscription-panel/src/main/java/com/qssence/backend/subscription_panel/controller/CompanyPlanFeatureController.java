package com.qssence.backend.subscription_panel.controller;

import com.qssence.backend.subscription_panel.dto.request.AssignPlanRequest;
import com.qssence.backend.subscription_panel.dto.request.PlanFeatureDto;
import com.qssence.backend.subscription_panel.dto.response.AssignPlanResponse;
import com.qssence.backend.subscription_panel.exception.ApiResponse;
import com.qssence.backend.subscription_panel.service.impl.CompanyPlanFeatureServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/company-plan")
@RequiredArgsConstructor
public class CompanyPlanFeatureController {

    private final CompanyPlanFeatureServiceImpl companyPlanFeatureService;

    // 1. Assign Plan
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<AssignPlanResponse>> assignPlanToCompany(@RequestBody AssignPlanRequest request) {
        try {
            for (PlanFeatureDto planDto : request.getPlans()) {
                boolean isPlanAssigned = companyPlanFeatureService.isPlanAlreadyAssigned(request.getCompanyId(), planDto.getPlanId());
                if (isPlanAssigned) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.<AssignPlanResponse>builder()
                            .success(false)
                            .message("❌ Plan with ID " + planDto.getPlanId() + " is already assigned to the company. Please update it instead.")
                            .data(null)
                            .build());
                }
            }

            AssignPlanResponse response = companyPlanFeatureService.assignPlanToCompany(request);
            return ResponseEntity.ok(ApiResponse.<AssignPlanResponse>builder()
                    .success(true)
                    .message("✅ Plan assigned successfully to the company.")
                    .data(response)
                    .build());

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<AssignPlanResponse>builder()
                    .success(false)
                    .message("❗ Entity not found: " + ex.getMessage())
                    .data(null)
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<AssignPlanResponse>builder()
                    .success(false)
                    .message("❌ Invalid data: " + ex.getMessage())
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<AssignPlanResponse>builder()
                    .success(false)
                    .message("⚠️ Error while assigning plan: " + e.getMessage())
                    .data(null)
                    .build());
        }
    }

    // 2. Get All Companies with Plans
    @GetMapping("/companies-with-plans/getAll")
    public ResponseEntity<ApiResponse<List<AssignPlanResponse>>> getAllCompaniesWithPlans() {
        try {
            List<AssignPlanResponse> responses = companyPlanFeatureService.getAllCompaniesWithPlans();
            String message = responses.isEmpty()
                    ? "ℹ️ No companies with assigned plans found."
                    : "✅ Companies with assigned plans retrieved successfully.";

            return ResponseEntity.ok(ApiResponse.<List<AssignPlanResponse>>builder()
                    .success(true)
                    .message(message)
                    .data(responses)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<List<AssignPlanResponse>>builder()
                    .success(false)
                    .message("⚠️ Error while fetching companies: " + e.getMessage())
                    .data(null)
                    .build());
        }
    }

    // 3. Update Assigned Plan
    @PutMapping("/update-plan/{companyId}")
    public ResponseEntity<ApiResponse<AssignPlanResponse>> updateCompanyPlan(
            @PathVariable String companyId,
            @RequestBody AssignPlanRequest request) {
        try {
            AssignPlanResponse response = companyPlanFeatureService.updateCompanyPlan(companyId, request);
            return ResponseEntity.ok(ApiResponse.<AssignPlanResponse>builder()
                    .success(true)
                    .message("✅ Plan updated successfully for company: " + companyId)
                    .data(response)
                    .build());

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<AssignPlanResponse>builder()
                            .success(false)
                            .message("❗ Entity not found: " + ex.getMessage())
                            .data(null)
                            .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AssignPlanResponse>builder()
                            .success(false)
                            .message("❌ Invalid update data: " + ex.getMessage())
                            .data(null)
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<AssignPlanResponse>builder()
                            .success(false)
                            .message("⚠️ An error occurred while updating the plan: " + ex.getMessage())
                            .data(null)
                            .build());
        }
    }

    // 4. Remove All Plans from a Company
    @DeleteMapping("/remove-all/{companyId}")
    public ResponseEntity<ApiResponse<String>> removeAllPlansFromCompany(@PathVariable String companyId) {
        try {
            companyPlanFeatureService.removeAllPlansFromCompany(companyId);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("🗑️ All plans removed from company: " + companyId)
                    .data("All assigned plans removed successfully.")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<String>builder()
                    .success(false)
                    .message("⚠️ Error while removing plans: " + e.getMessage())
                    .data(null)
                    .build());
        }
    }
}

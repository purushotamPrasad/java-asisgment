package com.qssence.backend.subscription_panel.controller.plansController;

import com.qssence.backend.subscription_panel.dto.request.AssignPlanRequest;
import com.qssence.backend.subscription_panel.dto.request.PlanRequest;
import com.qssence.backend.subscription_panel.dto.response.PlanResponse;
import com.qssence.backend.subscription_panel.exception.ApiResponse;
import com.qssence.backend.subscription_panel.service.plansService.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v2/plans")
public class PlanController {

    @Autowired
    private PlanService planService;

    // Create a new plan
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PlanResponse>> createPlan(@RequestBody PlanRequest planRequest) {
        try {
            PlanResponse planResponse = planService.createPlan(planRequest);
            ApiResponse<PlanResponse> response = ApiResponse.<PlanResponse>builder()
                    .success(true)
                    .message("Plan created successfully")
                    .data(planResponse)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse<PlanResponse> response = ApiResponse.<PlanResponse>builder()
                    .success(false)
                    .message("Failed to create plan: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Get all plans
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getAllPlans() {
        try {
            List<PlanResponse> plans = planService.getAllPlans();
            ApiResponse<List<PlanResponse>> response = ApiResponse.<List<PlanResponse>>builder()
                    .success(true)
                    .message("All plans retrieved successfully")
                    .data(plans)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<PlanResponse>> response = ApiResponse.<List<PlanResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve plans: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get plan by ID
    @GetMapping("/getById/{id}")
    public ResponseEntity<ApiResponse<PlanResponse>> getPlanById(@PathVariable Long id) {
        try {
            PlanResponse plan = planService.getPlanById(id);
            ApiResponse<PlanResponse> response = ApiResponse.<PlanResponse>builder()
                    .success(true)
                    .message("Plan retrieved successfully")
                    .data(plan)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<PlanResponse> response = ApiResponse.<PlanResponse>builder()
                    .success(false)
                    .message("Failed to retrieve plan: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Update an existing plan by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<PlanResponse>> updatePlan(
            @PathVariable Long id,
            @RequestBody PlanRequest planRequest) {
        try {
            PlanResponse updatedPlan = planService.updatePlan(id, planRequest);
            ApiResponse<PlanResponse> response = ApiResponse.<PlanResponse>builder()
                    .success(true)
                    .message("Plan updated successfully")
                    .data(updatedPlan)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<PlanResponse> response = ApiResponse.<PlanResponse>builder()
                    .success(false)
                    .message("Failed to update plan: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Delete plan by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deletePlanById(@PathVariable Long id) {
        try {
            planService.deletePlanById(id);
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(true)
                    .message("Plan with ID " + id + " has been deleted successfully")
                    .data(null)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to delete plan: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
//
//    @PostMapping("/assign/{companyId}")
//    public ResponseEntity<ApiResponse<List<PlanResponse>>> assignPlansToCompany(
//            @RequestBody List<Long> planIds,
//            @PathVariable String companyId) {
//        ApiResponse<List<PlanResponse>> response = planService.assignPlansToCompany(planIds, companyId);
//        return ResponseEntity.ok(response);
//    }

    // Assign plans with features to a company
//    @PreAuthorize("hasRole('ADMIN')")
//

}


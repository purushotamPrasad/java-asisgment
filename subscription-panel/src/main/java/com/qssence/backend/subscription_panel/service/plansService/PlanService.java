package com.qssence.backend.subscription_panel.service.plansService;

import com.qssence.backend.subscription_panel.dbo.Company;
import com.qssence.backend.subscription_panel.dbo.plans.Feature;
import com.qssence.backend.subscription_panel.dbo.plans.Plan;
import com.qssence.backend.subscription_panel.dto.request.AssignPlanRequest;
import com.qssence.backend.subscription_panel.dto.request.FeatureRequest;
import com.qssence.backend.subscription_panel.dto.request.PlanRequest;
import com.qssence.backend.subscription_panel.dto.response.FeatureResponse;
import com.qssence.backend.subscription_panel.dto.response.PlanResponse;
import com.qssence.backend.subscription_panel.exception.ApiResponse;
import com.qssence.backend.subscription_panel.exception.ResourceNotFoundException;
import com.qssence.backend.subscription_panel.repository.CompanyRepository;
import com.qssence.backend.subscription_panel.repository.plansRepo.FeatureRepository;
import com.qssence.backend.subscription_panel.repository.plansRepo.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private FeatureRepository featureRepository;

    // Create a new plan
    public PlanResponse createPlan(PlanRequest planRequest) {
        Plan plan = new Plan();
        plan.setName(planRequest.getName());
        plan.setDescription(planRequest.getDescription());

        List<Feature> features = planRequest.getFeatures().stream().map(featureName -> {
            Feature feature = new Feature();
            feature.setName(featureName.getName());
            feature.setPlan(plan); // Associate feature with the plan
            return feature;
        }).collect(Collectors.toList());

        plan.setFeatures(features);

        // Save the plan and features in the database
        Plan savedPlan = planRepository.save(plan);
        return mapToPlanResponse(savedPlan);
    }
    // Get all plans
    public List<PlanResponse> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::mapToPlanResponse)
                .collect(Collectors.toList());
    }

    // Get plan by ID
    public PlanResponse getPlanById(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan with ID " + id + " not found"));
        return mapToPlanResponse(plan);
    }

    public PlanResponse updatePlan(Long id, PlanRequest planRequest) {
        // Fetch the existing plan
        Plan existingPlan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan with ID " + id + " not found"));

        // Check if the name is being updated and is already in use by another plan
        if (!existingPlan.getName().equals(planRequest.getName())
                && planRepository.existsByName(planRequest.getName())) {
            throw new IllegalArgumentException("Plan with name '" + planRequest.getName() + "' already exists.");
        }

        // Update basic fields
        existingPlan.setName(planRequest.getName());
        existingPlan.setDescription(planRequest.getDescription());

        // Get existing features
        List<Feature> existingFeatures = existingPlan.getFeatures();

        // Extract requested feature IDs from the request
        List<Long> requestedFeatureIds = planRequest.getFeatures().stream()
                .map(FeatureRequest::getFeaturesId)
                .collect(Collectors.toList());

        // Remove features that are not in the request
        existingFeatures.removeIf(feature -> !requestedFeatureIds.contains(feature.getFeaturesId()));

        // Update existing features and add new ones
        for (FeatureRequest featureRequest : planRequest.getFeatures()) {
            if (featureRequest.getFeaturesId() != null) {
                // Update existing features
                existingFeatures.stream()
                        .filter(feature -> feature.getFeaturesId().equals(featureRequest.getFeaturesId()))
                        .forEach(feature -> feature.setName(featureRequest.getName()));
            } else {
                // Add new features
                Feature newFeature = new Feature();
                newFeature.setName(featureRequest.getName());
                newFeature.setPlan(existingPlan);
                existingFeatures.add(newFeature);
            }
        }

        // Save the updated plan
        existingPlan.setFeatures(existingFeatures);
        Plan updatedPlan = planRepository.save(existingPlan);

        return mapToPlanResponse(updatedPlan);
    }


    // Delete plan by ID
    public void deletePlanById(Long id) {
        if (!planRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plan with ID " + id + " does not exist");
        }
        planRepository.deleteById(id);
    }

    // Helper method to map Plan to PlanResponse
    private PlanResponse mapToPlanResponse(Plan plan) {
        List<FeatureResponse> features = plan.getFeatures().stream()
                .map(feature -> new FeatureResponse(feature.getFeaturesId(), feature.getName()))
                .collect(Collectors.toList());

        return new PlanResponse(
                plan.getPlanId(),
                plan.getName(),
                plan.getDescription(),
                features
        );
    }

//
//    public ApiResponse<List<PlanResponse>> assignPlansWithFeaturesToCompany(
//            List<AssignPlanRequest> planRequests, String companyId) {
//
//        // Fetch Company
//        Company company = companyRepository.findById(companyId)
//                .orElseThrow(() -> new ResourceNotFoundException("Company with ID " + companyId + " not found"));
//
//        List<PlanResponse> assignedPlans = new ArrayList<>();
//
//        for (AssignPlanRequest request : planRequests) {
//            // Fetch Plan
//            Plan plan = planRepository.findById(request.getPlanId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Plan with ID " + request.getPlanId() + " not found"));
//
//            // Filter Features based on featureIds in the request
//            List<Feature> selectedFeatures = plan.getFeatures().stream()
//                    .filter(feature -> request.getFeatureIds().contains(feature.getFeaturesId()))
//                    .collect(Collectors.toList());
//
//            // Create a filtered plan (Make sure plan is already in the DB)
//            Plan filteredPlan = new Plan();
//            filteredPlan.setPlanId(plan.getPlanId());  // Use existing plan's ID
//            filteredPlan.setName(plan.getName());
//            filteredPlan.setDescription(plan.getDescription());
//            filteredPlan.setFeatures(selectedFeatures);
//
//            // Add filtered plan to company's plans (make sure to update the company object with the plan)
//            company.getPlans().add(filteredPlan);
//
//            // Convert the filtered plan to a response object
//            PlanResponse response = mapToPlanResponse(filteredPlan);
//            assignedPlans.add(response);
//        }
//
//        // Save the company with the newly assigned plans
//        companyRepository.save(company);
//
//        // Return the response with all assigned plans
//        return new ApiResponse<>(true, "Plans with selected features assigned to company successfully", assignedPlans);
//    }
//

}
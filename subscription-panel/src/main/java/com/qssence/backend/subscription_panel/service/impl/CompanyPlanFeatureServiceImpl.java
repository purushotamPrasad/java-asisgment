package com.qssence.backend.subscription_panel.service.impl;

import com.qssence.backend.subscription_panel.dbo.Company;
import com.qssence.backend.subscription_panel.dbo.plans.CompanyPlanFeature;
import com.qssence.backend.subscription_panel.dbo.plans.Feature;
import com.qssence.backend.subscription_panel.dbo.plans.Plan;
import com.qssence.backend.subscription_panel.dto.request.AssignPlanRequest;
import com.qssence.backend.subscription_panel.dto.request.PlanFeatureDto;
import com.qssence.backend.subscription_panel.dto.response.AssignPlanResponse;
import com.qssence.backend.subscription_panel.dto.response.FeatureResponse;
import com.qssence.backend.subscription_panel.dto.response.PlanResponse;
import com.qssence.backend.subscription_panel.repository.CompanyRepository;
import com.qssence.backend.subscription_panel.repository.plansRepo.CompanyPlanFeatureRepository;
import com.qssence.backend.subscription_panel.repository.plansRepo.FeatureRepository;
import com.qssence.backend.subscription_panel.repository.plansRepo.PlanRepository;
import com.qssence.backend.subscription_panel.service.CompanyPlanFeatureService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyPlanFeatureServiceImpl implements CompanyPlanFeatureService {

    private final CompanyRepository companyRepository;
    private final PlanRepository planRepository;
    private final FeatureRepository featureRepository;
    private final CompanyPlanFeatureRepository companyPlanFeatureRepository;

    @Override
    @Transactional
    public AssignPlanResponse assignPlanToCompany(AssignPlanRequest request) {
        Company company = getCompanyById(request.getCompanyId());

        // Remove all existing plan-feature assignments for the company
        companyPlanFeatureRepository.deleteByCompany(company);

        // Assign new plans and features
        for (PlanFeatureDto planDto : request.getPlans()) {
            Plan plan = getPlanById(planDto.getPlanId());
            List<Feature> features = featureRepository.findAllById(planDto.getFeatureIds());

            List<CompanyPlanFeature> companyPlanFeatures = features.stream().map(feature -> {
                CompanyPlanFeature cpf = new CompanyPlanFeature();
                cpf.setCompany(company);
                cpf.setPlan(plan);
                cpf.setFeature(feature);
                return cpf;
            }).collect(Collectors.toList());

            companyPlanFeatureRepository.saveAll(companyPlanFeatures);
        }

        return buildAssignPlanResponse(company);
    }

    @Override
    @Transactional
    public AssignPlanResponse updateCompanyPlan(String companyId, AssignPlanRequest request) {
        Company company = getCompanyById(companyId);

        // 🔴 1. Saare existing plan-feature entries delete karo (purge approach)
        companyPlanFeatureRepository.deleteByCompany(company);

        // 🔵 2. Naye plans assign karo
        for (PlanFeatureDto planDto : request.getPlans()) {
            Plan plan = getPlanById(planDto.getPlanId());
            List<Feature> features = featureRepository.findAllById(planDto.getFeatureIds());

            List<CompanyPlanFeature> companyPlanFeatures = features.stream().map(feature -> {
                CompanyPlanFeature cpf = new CompanyPlanFeature();
                cpf.setCompany(company);
                cpf.setPlan(plan);
                cpf.setFeature(feature);
                return cpf;
            }).collect(Collectors.toList());

            companyPlanFeatureRepository.saveAll(companyPlanFeatures);
        }

        return buildAssignPlanResponse(company);
    }





    public boolean isPlanAlreadyAssigned(String companyId, Long planId) {
        Company company = getCompanyById(companyId);
        Plan plan = getPlanById(planId);

        // Check if company already has the same plan assigned
        return companyPlanFeatureRepository.existsByCompanyAndPlan(company, plan);
    }




    @Override
    public List<AssignPlanResponse> getAllCompaniesWithPlans() {
        List<CompanyPlanFeature> companyPlans = companyPlanFeatureRepository.findAll();

        Map<Company, Map<Plan, List<Feature>>> groupedByCompany = companyPlans.stream()
                .collect(Collectors.groupingBy(
                        CompanyPlanFeature::getCompany,
                        Collectors.groupingBy(
                                CompanyPlanFeature::getPlan,
                                Collectors.mapping(CompanyPlanFeature::getFeature, Collectors.toList())
                        )
                ));

        return groupedByCompany.entrySet().stream().map(entry -> {
            Company company = entry.getKey();
            List<PlanResponse> plans = entry.getValue().entrySet().stream().map(planEntry -> {
                Plan plan = planEntry.getKey();
                List<FeatureResponse> features = planEntry.getValue().stream()
                        .map(feature -> new FeatureResponse(feature.getFeaturesId(), feature.getName()))
                        .collect(Collectors.toList());

                return new PlanResponse(plan.getPlanId(), plan.getName(), plan.getDescription(), features);
            }).collect(Collectors.toList());

            return AssignPlanResponse.builder()
                    .id(company.getCompanyId())
                    .companyName(company.getCompanyName())
                    .location(company.getLocation())
                    .email(company.getCompanyEmailId())
                    .plans(plans) // 🟢 Multiple plans added
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeAllPlansFromCompany(String companyId) {
        Company company = getCompanyById(companyId);

        // Company ke saare assigned plans aur features delete karo
        companyPlanFeatureRepository.deleteByCompany(company);
    }

//    private Company getCompanyById(String companyId) {
//        return companyRepository.findById(companyId)
//                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));
//    }

    private Company getCompanyById(String companyId) {
        System.out.println("Fetching company with ID: " + companyId);
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));
    }


    private Plan getPlanById(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with ID: " + planId));
    }


    private AssignPlanResponse buildAssignPlanResponse(Company company) {
        List<CompanyPlanFeature> companyPlanFeatures = companyPlanFeatureRepository.findByCompany(company);

        Map<Plan, List<Feature>> groupedByPlan = companyPlanFeatures.stream()
                .collect(Collectors.groupingBy(
                        CompanyPlanFeature::getPlan,
                        Collectors.mapping(CompanyPlanFeature::getFeature, Collectors.toList())
                ));

        List<PlanResponse> plans = groupedByPlan.entrySet().stream().map(entry -> {
            Plan plan = entry.getKey();
            List<FeatureResponse> features = entry.getValue().stream()
                    .map(feature -> new FeatureResponse(feature.getFeaturesId(), feature.getName()))
                    .collect(Collectors.toList());

            return new PlanResponse(plan.getPlanId(), plan.getName(), plan.getDescription(), features);
        }).collect(Collectors.toList());

        return AssignPlanResponse.builder()
                .id(company.getCompanyId())
                .companyName(company.getCompanyName())
                .location(company.getLocation())
                .email(company.getCompanyEmailId())
                .plans(plans) // 🟢 Multiple plans returned
                .build();
    }

}

package com.qssence.backend.subscription_panel.service;

import com.qssence.backend.subscription_panel.dto.request.AssignPlanRequest;
import com.qssence.backend.subscription_panel.dto.response.AssignPlanResponse;

import java.util.List;

public interface CompanyPlanFeatureService {

    // Company ko plan assign karne ke liye
    AssignPlanResponse assignPlanToCompany(AssignPlanRequest request);

    // Company ke plan ko update karne ke liye (existing plan ko replace karne ke liye)
    AssignPlanResponse updateCompanyPlan(String  companyId, AssignPlanRequest request);

    // Company ke ek specific plan ko remove karne ke liye
//    void removePlanFromCompany(String  companyId, Long planId);

    // Company ke sabhi plans ko remove karne ke liye
    void removeAllPlansFromCompany(String  companyId);


    List<AssignPlanResponse> getAllCompaniesWithPlans();

}

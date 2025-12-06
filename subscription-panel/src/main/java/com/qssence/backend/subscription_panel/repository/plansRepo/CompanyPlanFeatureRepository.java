package com.qssence.backend.subscription_panel.repository.plansRepo;

import com.qssence.backend.subscription_panel.dbo.Company;
import com.qssence.backend.subscription_panel.dbo.plans.CompanyPlanFeature;
import com.qssence.backend.subscription_panel.dbo.plans.Feature;
import com.qssence.backend.subscription_panel.dbo.plans.Plan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CompanyPlanFeatureRepository extends JpaRepository<CompanyPlanFeature, Long> {

//    @Transactional
//    void deleteByCompany(Company company);  // Ensure this method exists
//
//    @Transactional
//    void deleteByCompanyAndPlan(Company company, Plan plan);  // For specific plan removal
//
//    List<CompanyPlanFeature> findByCompanyAndPlanId(Company company, Long planId);

    List<CompanyPlanFeature> findByCompany(Company company);
    void deleteByCompany(Company company);
    boolean existsByCompanyAndPlan(Company company, Plan plan);

    List<CompanyPlanFeature> findByCompanyAndPlan(Company company, Plan plan);

    @Transactional
    @Modifying
    @Query("DELETE FROM CompanyPlanFeature cpf WHERE cpf.company = :company AND cpf.plan = :plan AND cpf.feature.featuresId IN :featureIds")
    void deleteByCompanyAndPlanAndFeatureIds(@Param("company") Company company,
                                             @Param("plan") Plan plan,
                                             @Param("featureIds") Set<Long> featureIds);



}

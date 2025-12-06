package com.qssence.backend.subscription_panel.repository.plansRepo;


import com.qssence.backend.subscription_panel.dbo.plans.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    boolean existsByName(String name);
}

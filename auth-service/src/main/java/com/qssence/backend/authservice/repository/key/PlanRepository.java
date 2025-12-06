package com.qssence.backend.authservice.repository.key;

import com.qssence.backend.authservice.dbo.Key.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}

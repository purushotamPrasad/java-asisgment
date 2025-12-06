package com.qssence.backend.subscription_panel.repository.plansRepo;

import com.qssence.backend.subscription_panel.dbo.plans.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {


}

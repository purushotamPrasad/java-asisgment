package com.qssence.backend.authservice.repository.key;

import com.qssence.backend.authservice.dbo.Key.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
}

package com.qssence.backend.authservice.repository.Plants;

import com.qssence.backend.authservice.dbo.Plants.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section,Long> {

    Optional<Section> findBySectionName(String sectionName);
}

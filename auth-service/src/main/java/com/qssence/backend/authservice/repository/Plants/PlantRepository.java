package com.qssence.backend.authservice.repository.Plants;


import com.qssence.backend.authservice.dbo.Plants.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PlantRepository extends JpaRepository<Plant, Long> {
    Optional<Plant> findByPlantName(String plantName);
}

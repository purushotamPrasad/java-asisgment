package com.qssence.backend.authservice.service.implementation.plants;


import com.qssence.backend.authservice.dbo.Plants.Department;
import com.qssence.backend.authservice.dbo.Plants.Plant;
import com.qssence.backend.authservice.dbo.Plants.Section;
import com.qssence.backend.authservice.dto.Plants.*;
import com.qssence.backend.authservice.exception.ResourceNotFoundException;
import com.qssence.backend.authservice.repository.Plants.DepartmentRepository;
import com.qssence.backend.authservice.repository.Plants.PlantRepository;
import com.qssence.backend.authservice.repository.Plants.SectionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlantService {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    // Create plant
    public Plant createPlant(PlantRequest plantRequest) {
        // Create the plant
        Plant plant = new Plant();
        plant.setPlantName(plantRequest.getPlantName());
        plant.setRegion(plantRequest.getRegion());
        plant.setCountry(plantRequest.getCountry());
        plant.setLocation(plantRequest.getLocation());

        // Map departments
        List<Department> departments = plantRequest.getDepartment().stream().map(departmentRequest -> {
            Department department = new Department();
            department.setDepartmentName(departmentRequest.getDepartmentName());
            department.setPlant(plant);

            // Map sections
            List<Section> sections = departmentRequest.getSection().stream().map(sectionRequest -> {
                Section section = new Section();
                section.setSectionName(sectionRequest.getSectionName());
                section.setDepartment(department);
                return section;
            }).collect(Collectors.toList());

            department.setSections(sections);
            return department;
        }).collect(Collectors.toList());

        plant.setDepartment(departments);

        // Save the plant (cascading will save departments and sections)
        return plantRepository.save(plant);
    }

    // Get all plants
    public List<PlantResponse> getAllPlants() {
        return plantRepository.findAll().stream().map(plant -> {
            PlantResponse response = new PlantResponse();
            response.setId(plant.getId());
            response.setPlantName(plant.getPlantName());
            response.setRegion(plant.getRegion());
            response.setCountry(plant.getCountry());
            response.setLocation(plant.getLocation());
            // Map departments with sections
            List<DepartmentResponse> departmentResponses = plant.getDepartment().stream().map(department -> {
                DepartmentResponse departmentResponse = new DepartmentResponse();
                departmentResponse.setDepartmentId(department.getDepartmentId());
                departmentResponse.setDepartmentName(department.getDepartmentName());

                // Map sections
                List<SectionResponse> sectionResponses = department.getSections().stream().map(section -> {
                    SectionResponse sectionResponse = new SectionResponse();
                    sectionResponse.setId(section.getSectionId());
                    sectionResponse.setSectionName(section.getSectionName());
                    return sectionResponse;
                }).collect(Collectors.toList());

                departmentResponse.setSection(sectionResponses);
                return departmentResponse;
            }).collect(Collectors.toList());

            response.setDepartment(departmentResponses);
            return response;
        }).collect(Collectors.toList());
    }

    // Get a plant by ID
    public Optional<PlantResponse> getPlantById(Long plantId) {
        return plantRepository.findById(plantId).map(plant -> {
            PlantResponse response = new PlantResponse();
            response.setId(plant.getId());
            response.setPlantName(plant.getPlantName());
            response.setRegion(plant.getRegion());
            response.setCountry(plant.getCountry());
            response.setLocation(plant.getLocation());
            // Map departments with their sections
            List<DepartmentResponse> departmentResponses = plant.getDepartment().stream().map(department -> {
                DepartmentResponse departmentResponse = new DepartmentResponse();
                departmentResponse.setDepartmentId(department.getDepartmentId());
                departmentResponse.setDepartmentName(department.getDepartmentName());

                // Map sections to SectionResponse
                List<SectionResponse> sectionResponses = department.getSections().stream().map(section -> {
                    SectionResponse sectionResponse = new SectionResponse();
                    sectionResponse.setId(section.getSectionId());
                    sectionResponse.setSectionName(section.getSectionName());
                    return sectionResponse;
                }).collect(Collectors.toList());

                departmentResponse.setSection(sectionResponses);
                return departmentResponse;
            }).collect(Collectors.toList());

            response.setDepartment(departmentResponses);
            return response;
        });
    }


    @Transactional
    public Plant deletePlant(Long plantId) {
        Plant existingPlant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with ID: " + plantId));

        plantRepository.delete(existingPlant);
        return existingPlant;
    }

    @Transactional
    public Plant updatePlant(Long plantId, PlantRequest plantRequest) {
        Plant existingPlant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        // Update plant fields
        existingPlant.setPlantName(plantRequest.getPlantName());
        existingPlant.setRegion(plantRequest.getRegion());
        existingPlant.setCountry(plantRequest.getCountry());
        existingPlant.setLocation(plantRequest.getLocation());

        // Update departments
        Map<Long, Department> existingDepartmentsMap = existingPlant.getDepartment().stream()
                .collect(Collectors.toMap(Department::getDepartmentId, dept -> dept));

        List<Department> updatedDepartments = plantRequest.getDepartment().stream().map(deptRequest -> {
            Department department = existingDepartmentsMap.get(deptRequest.getDepartmentId());
            if (department == null) {
                department = new Department(); // Create a new department if not found
                department.setPlant(existingPlant);
            }

            department.setDepartmentName(deptRequest.getDepartmentName());

            // Update sections
            Map<Long, Section> existingSectionsMap = department.getSections().stream()
                    .collect(Collectors.toMap(Section::getSectionId, sec -> sec));

            Department finalDepartment = department;
            List<Section> updatedSections = deptRequest.getSection().stream().map(secRequest -> {
                Section section = existingSectionsMap.get(secRequest.getId());
                if (section == null) {
                    section = new Section(); // Create a new section if not found
                    section.setDepartment(finalDepartment);
                }

                section.setSectionName(secRequest.getSectionName());
                return section;
            }).collect(Collectors.toList());

            // Explicitly delete sections that are not in the request
            department.getSections().forEach(existingSection -> {
                if (updatedSections.stream().noneMatch(sec -> sec.getSectionId().equals(existingSection.getSectionId()))) {
                    sectionRepository.delete(existingSection); // Explicitly delete the section
                }
            });

            department.setSections(updatedSections);
            return department;
        }).collect(Collectors.toList());

        // Explicitly delete departments that are not in the request
        existingPlant.getDepartment().forEach(existingDepartment -> {
            if (updatedDepartments.stream().noneMatch(dept -> dept.getDepartmentId().equals(existingDepartment.getDepartmentId()))) {
                departmentRepository.delete(existingDepartment); // Explicitly delete the department
            }
        });

        existingPlant.setDepartment(updatedDepartments);

        // Save the updated plant
        return plantRepository.save(existingPlant);
    }

}

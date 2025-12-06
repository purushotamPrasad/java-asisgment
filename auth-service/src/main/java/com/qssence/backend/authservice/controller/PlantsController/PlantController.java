package com.qssence.backend.authservice.controller.PlantsController;

import com.qssence.backend.authservice.dbo.Plants.Plant;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.Plants.*;
import com.qssence.backend.authservice.service.implementation.plants.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/plants")
public class PlantController {

    @Autowired
    private PlantService plantService;

//    @GetMapping("/getPlantWithCompany/{id}")
//    public ResponseEntity<PlantResponse> getPlantDetails(@PathVariable("id") Long id){
//        PlantResponse plantResponse = plantService.getPantWithCompanyById(id);
//        return ResponseEntity.status(HttpStatus.OK).body(plantResponse);
//    }

    // Create plant
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PlantResponse>> createPlant(@RequestBody PlantRequest plantRequest) {
        try {
            // Call the service to create the plant
            Plant plant = plantService.createPlant(plantRequest);

            // Prepare success response data
            PlantResponse responseData = new PlantResponse();
            responseData.setId(plant.getId());
            responseData.setPlantName(plant.getPlantName());
            responseData.setRegion(plant.getRegion());
            responseData.setCountry(plant.getCountry());
            responseData.setLocation(plant.getLocation());
            responseData.setDepartment(plant.getDepartment().stream().map(department -> {
                DepartmentResponse deptResponse = new DepartmentResponse();
                deptResponse.setDepartmentId(department.getDepartmentId());
                deptResponse.setDepartmentName(department.getDepartmentName());
                deptResponse.setSection(department.getSections().stream().map(section -> {
                    SectionResponse sectionResponse = new SectionResponse();
                    sectionResponse.setId(section.getSectionId());
                    sectionResponse.setSectionName(section.getSectionName());
                    return sectionResponse;
                }).collect(Collectors.toList()));
                return deptResponse;
            }).collect(Collectors.toList()));

            // Return success response
            ApiResponse<PlantResponse> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("Plant created/updated successfully.");
            apiResponse.setSuccess(true);
            apiResponse.setData(responseData);

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception ex) {
            // Log the error (optional)
            ex.printStackTrace();

            // Return failure response with the same generic type
            ApiResponse<PlantResponse> errorResponse = new ApiResponse<>();
            errorResponse.setMessage("Failed to create/update the plant: " + ex.getMessage());
            errorResponse.setSuccess(false);
            errorResponse.setData(null); // Setting `data` to `null` but type remains consistent

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get all plants
    @GetMapping("/getAll")
    public ResponseEntity<Map<String, Object>> getAllPlants() {
        List<PlantResponse> plants = plantService.getAllPlants();
        Map<String, Object> response = new LinkedHashMap<>(); // Use LinkedHashMap to preserve order

        response.put("message", plants.isEmpty() ? "No plants found." : "Plants retrieved successfully.");
        response.put("data", plants);

        return plants.isEmpty()
                ? ResponseEntity.status(404).body(response)
                : ResponseEntity.ok(response);
    }



    // Get plant by ID
    @GetMapping("/getById/{id}")
    public ResponseEntity<Map<String, Object>> getPlantById(@PathVariable Long id) {
        return plantService.getPlantById(id)
                .map(plantResponse -> {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("message", "Plant retrieved successfully.");
                    response.put("data", plantResponse);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", "Plant not found.");
                    return ResponseEntity.status(404).body(errorResponse);
                });
    }

    @DeleteMapping("/{plantId}")
    public ResponseEntity<ApiResponse> deletePlant(@PathVariable Long plantId) {
       try{
           plantService.deletePlant(plantId);
           PlantResponse responseData = new PlantResponse();
        // Return success response
        ApiResponse<PlantResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Plant "+plantId+" deleted successfully.");
        apiResponse.setSuccess(true);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    } catch (Exception ex) {
        // Log the error (optional)
        ex.printStackTrace();

        // Return failure response with the same generic type
        ApiResponse<PlantResponse> errorResponse = new ApiResponse<>();
        errorResponse.setMessage("Failed to delete the plant: " +plantId);
        errorResponse.setSuccess(false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    }

    // Update departments and sections by plant ID
//    @PutMapping("update/{plantId}")
//    public ResponseEntity<Plant> updatePlant(
//            @PathVariable Long plantId,
//            @RequestBody PlantRequest plantRequest) {
//        Plant updatedPlant = plantService.updatePlant(plantId, plantRequest);
//        return ResponseEntity.ok(updatedPlant);
//    }

    @PutMapping("/update/{plantId}")
    public ResponseEntity<Plant> updatePlant(@PathVariable Long plantId, @RequestBody PlantRequest plantRequest) {
        Plant updatedPlant = plantService.updatePlant(plantId, plantRequest);
        if (updatedPlant == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPlant);
    }

}

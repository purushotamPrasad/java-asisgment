package com.qssence.backend.authservice.repository.Plants;

import com.qssence.backend.authservice.dbo.Plants.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartmentName(String departmentName);
}

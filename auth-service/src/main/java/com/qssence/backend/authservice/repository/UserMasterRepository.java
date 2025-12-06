package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {

//    // Native query for company-wise last employeeId
//    @Query(value = "SELECT u.employee_id " +
//            "FROM user_master u " +
//            "WHERE u.employee_id LIKE CONCAT(:prefix, '%') " +
//            "ORDER BY u.employee_id DESC " +
//            "LIMIT 1",
//            nativeQuery = true)
//    String findLastEmployeeIdByPrefix(@Param("prefix") String prefix);

    @Query("SELECT u.employeeId FROM UserMaster u " +
            "WHERE u.employeeId LIKE CONCAT(:prefix, '%') " +
            "ORDER BY u.employeeId DESC LIMIT 1")
    String findLastEmployeeIdByPrefix(@Param("prefix") String prefix);

    // Count employees by company prefix
    @Query("SELECT COUNT(u) FROM UserMaster u " +
            "WHERE u.employeeId LIKE CONCAT(:prefix, '%')")
    Long countEmployeesByPrefix(@Param("prefix") String prefix);

}

package com.qssence.backend.authservice.dbo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_employee_sequence")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEmployeeSequence {

    @Id
    @Column(name = "prefix", nullable = false, unique = true)
    private String companyPrefix; // e.g. VED, CIP

    @Column(name = "last_number", nullable = false)
    private Long lastNumber;
}

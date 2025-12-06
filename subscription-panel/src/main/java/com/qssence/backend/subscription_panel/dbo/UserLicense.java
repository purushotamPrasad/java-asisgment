package com.qssence.backend.subscription_panel.dbo;

import com.qssence.backend.subscription_panel.dbo.plans.CompanyPlanFeature;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user_licenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long licenseId;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;


    // Fetching plans assigned to this company
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "company_id", insertable = false, updatable = false)
    private List<CompanyPlanFeature> companyPlans;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Column(nullable = false)
    private LocalDate expiryDate;


    @Column(nullable = false)
    private Long purchaseCost;  // 🔹 New field

    @Column(nullable = false)
    private Long totalUserAccess;  // 🔹 Total User Access Count

    @Column(nullable = false)
    private Long adminAccountAllowed;  // 🔹 Allowed Admin Accounts

    @Column(nullable = false)
    private Long userAccountAllowed;  // 🔹 Allowed User Accounts

    @Column(length = 500)
    private String description;

    @Column
    private boolean active;

}

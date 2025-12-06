package com.qssence.backend.subscription_panel.dbo;

import com.qssence.backend.subscription_panel.dbo.plans.CompanyPlanFeature;
import com.qssence.backend.subscription_panel.dbo.plans.Plan;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "company_details")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Company {

    @Id
    private String companyId;

    @Column(nullable = false)
    @NotBlank(message = "Company name is required")
    private String companyName;

    // ✅ NEW FIELD
    @Column(nullable = false, unique = true, length = 10)
    private String companyPrefix; // e.g. "QSS", "OAI"

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String region;

    @Column(name = "country")
    private String country;

    private String timezone;

    @Column(nullable = false)
    private String phoneNo;

    @Column(nullable = false)
    @NotBlank(message = "License number is required")
    private String licenseNo;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email ID is required")
    @Column(nullable = false)
    private String companyEmailId;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "company_plans",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "plan_id")
    )
    private List<Plan> plans = new ArrayList<>();


    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompanyPlanFeature> companyPlanFeatures = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @PrePersist
    protected void onCreate(){
        if (this.status == null) {
            this.status = Status.ACTIVE; // Default to ACTIVE before persisting
        }
    }
}

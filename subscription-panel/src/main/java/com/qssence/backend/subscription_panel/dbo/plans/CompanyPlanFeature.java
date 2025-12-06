package com.qssence.backend.subscription_panel.dbo.plans;


import com.qssence.backend.subscription_panel.dbo.Company;
import com.qssence.backend.subscription_panel.dbo.plans.Feature;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Company_Plan_Feature")
public class CompanyPlanFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.EAGER)  // Ensure feature is loaded
    @JoinColumn(name = "feature_id")
    private Feature feature;

    // ✅ Custom Constructor Added
    public CompanyPlanFeature(Company company, Plan plan, Feature feature) {
        this.company = company;
        this.plan = plan;
        this.feature = feature;
    }
}

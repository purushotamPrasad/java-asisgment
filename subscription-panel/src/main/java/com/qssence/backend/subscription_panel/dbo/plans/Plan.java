package com.qssence.backend.subscription_panel.dbo.plans;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.qssence.backend.subscription_panel.dbo.Company;
import com.qssence.backend.subscription_panel.dbo.plans.Feature;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Plans")
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long planId;

    @Column(nullable = false, unique = true) // Ensure unique plan names
    private String name;

    private String description; // Optional field for more information about the plan

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Feature> features = new ArrayList<>(); // A plan has multiple features

//    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Feature> features;
//
    @ManyToMany
    @JoinTable(
            name = "company_plans", // Junction table name
            joinColumns = @JoinColumn(name = "plan_id"), // FK for Plan
            inverseJoinColumns = @JoinColumn(name = "company_id") // FK for Company
    )
    private List<Company> companies = new ArrayList<>(); // A plan can belong to multiple companies

}


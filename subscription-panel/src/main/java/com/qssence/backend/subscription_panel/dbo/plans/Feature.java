package com.qssence.backend.subscription_panel.dbo.plans;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Features")
@Builder
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long featuresId;

    @Column(nullable = false) // Feature name cannot be null
    private String name;

    @ManyToOne
    @JoinColumn(name = "plan_id") // Foreign key to Plan
    private Plan plan; // Each feature is associated with one plan

}

package com.qssence.backend.authservice.dbo.Key;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Plan {
    @Id
    private Long planId;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "imported_license_id")
    private ImportedLicense importedLicense;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Feature> features;
}


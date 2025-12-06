package com.qssence.backend.authservice.dbo.Key;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Feature {
    @Id
    private Long featuresId;
    private String name;
}
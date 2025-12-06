package com.qssence.backend.document_initiation_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataHeading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private DocumentType documentType;

    @ManyToOne
    private SubType subType;

    @ManyToOne
    private Classification classification;

    @Column(nullable = false)
    private String headingName;

    private Integer displayOrder; // For ordering headings

    @OneToMany(mappedBy = "metadataHeading", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetadataField> metadataFields;
} 
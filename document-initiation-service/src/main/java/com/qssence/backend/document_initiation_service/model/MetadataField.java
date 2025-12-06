package com.qssence.backend.document_initiation_service.model;

import com.qssence.backend.document_initiation_service.enums.FieldType;
import com.qssence.backend.document_initiation_service.enums.FieldWidth;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "metadata_heading_id")
    private MetadataHeading metadataHeading;

    @Column(nullable = false)
    private String fieldName;

    @Enumerated(EnumType.STRING)
    private FieldType fieldType;

    @Enumerated(EnumType.STRING)
    private FieldWidth width;

    private Boolean required;

    private Integer displayOrder; // For ordering fields within a heading

    // --- Extra fields for dynamic attributes ---
    // For Text & Numeric
    private Long minLength;
    private Long maxLength;

    // For Text
    private String selectLine; // "single" or "multiple"

    // For Dropdown, Checkbox, Radio
    @ElementCollection
    @CollectionTable(name = "metadata_field_options", joinColumns = @JoinColumn(name = "metadata_field_id"))
    @Column(name = "option_value")
    private List<String> options;
}

package com.qssence.backend.document_initiation_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private DocumentType documentType;

    @ManyToOne
    private SubType subType;

    @ManyToOne
    private Classification classification;

    private String templateFile;
    private String s3Url;
    private LocalDateTime uploadedAt;

}

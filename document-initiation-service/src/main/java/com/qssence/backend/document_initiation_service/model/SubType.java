package com.qssence.backend.document_initiation_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "subType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Classification> classifications = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "document_type_id")
    private DocumentType documentType;



}

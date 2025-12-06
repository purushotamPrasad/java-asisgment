package com.qssence.backend.authservice.dto.request;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID rolesId;

    @NotBlank(message = "Role name is required")
    @Size(max = 60, message = "Role name must be at most 60 characters long")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Role name must contain only alphabets and spaces")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;

}
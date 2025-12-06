package com.qssence.backend.authservice.dbo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import com.qssence.backend.authservice.dbo.Permission.Permission;

@Entity
@Table(name = "Groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupsId;

    @NotBlank(message = "Group name is required")
    @Size(max = 60, message = "Group name must be at most 60 characters long")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Group name must contain only alphabets and spaces")
    private String name;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "user_groups",  // The join table
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserMaster> users = new HashSet<>(); // Initialized to prevent null

    // Enhanced: Role assignment to groups
    @ManyToMany
    @JoinTable(
            name = "group_roles",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> roles = new HashSet<>();

    // Enhanced: Group permissions
//    @ManyToMany
//    @JoinTable(
//            name = "group_permissions",
//            joinColumns = @JoinColumn(name = "group_id"),
//            inverseJoinColumns = @JoinColumn(name = "permission_id")
//    )
//    private Set<Permission.Permission> permissions = new HashSet<>();

    private String status;
    
    // Enhanced: Group metadata
    private String createdBy;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private String updatedBy; // New field for status


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;  // Corrected the class name
        return Objects.equals(groupsId, group.groupsId); // Corrected field name
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupsId); // Corrected field name
    }

}



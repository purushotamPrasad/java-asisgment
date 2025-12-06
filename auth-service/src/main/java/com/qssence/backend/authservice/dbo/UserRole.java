package com.qssence.backend.authservice.dbo;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qssence.backend.authservice.dbo.Permission.Permission;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Roles")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
@JsonIgnoreProperties({"users"})  // Ignore 'role' field during serialization
public class UserRole {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@SequenceGenerator(name="user_role_masterseq", sequenceName="user_role_masterseq", allocationSize=1)
	@Column(name="user_role_id")
	private Long userRoleId;

	@Column(name="user_role_name")
	private String userRoleName;

	@Column(name="userRole_description")
	private String description;

//Add role in user
	@ManyToMany(mappedBy = "role")  // 'role' refers to the 'role' field in UserMaster entity
	@JsonBackReference  // Avoid circular reference on back side
	private Set<UserMaster> users;

	private String status; // New field for status



	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserRole userRole = (UserRole) o;
		return Objects.equals(userRoleId, userRole.userRoleId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userRoleId);
	}

	public Object getRoleId() {
		return userRoleId;
	}
}

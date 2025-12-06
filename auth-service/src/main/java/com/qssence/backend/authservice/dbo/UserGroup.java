package com.qssence.backend.authservice.dbo;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "G")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID UserGroupId;
	private String userGroupName;
	private String userPermissionSchima;

}

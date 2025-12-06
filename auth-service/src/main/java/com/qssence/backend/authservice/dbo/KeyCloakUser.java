package com.qssence.backend.authservice.dbo;

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
@Table(name = "user_entity")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class KeyCloakUser {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	private String email;
	private String email_constraint;
	private boolean email_verified;
	private boolean enabled;
	private String federation_link;
	private String first_name;
	private String last_name;
	private String realm_id;
	private String username;
	private long created_timestamp;
	private String service_account_client_link;
	private int not_before;
	private String password;
}
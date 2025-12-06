package com.qssence.backend.authservice.dbo;

import java.time.ZoneId;
import java.util.*;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.qssence.backend.authservice.dbo.Plants.Department;
import com.qssence.backend.authservice.dbo.Plants.Plant;

import com.qssence.backend.authservice.dbo.Plants.Section;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CountryOnly;

import javax.swing.plaf.synth.Region;

@Entity
@Table(name = "user_master")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonIgnoreProperties({"role"})  // Ignore 'role' field during serialization
public class UserMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "user_first_name")
	private String firstName;

	@Column(name = "user_middle_name")
	private String middleName;

	@Column(name = "user_last_name")
	private String lastName;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Column(name = "user_mobile_number")
	private String mobileNumber;

	@Email(message = "Invalid email format")
	@Column(name = "user_email_id", nullable = false, unique = true)
	private String emailId;

	@Column(name = "user_address")
	private String address;

	@Column(name = "user_country")
	private String country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_plant_id") // foreign key column
	private Plant plant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_department_id") // foreign key column
	private Department department; // Assuming you have a Division entity

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_section_id") // foreign key column
	private Section section; // Assuming you have a Division entity


	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "user_groups",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "group_id")
	)
	private Set<Group> groups = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "user_roles",  // Corrected the join table name to match your UserRole entity
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	@JsonManagedReference
	private Set<UserRole> role = new HashSet<>();  // Initialize to avoid null


	@Column(name = "user_employee_id")
	private String employeeId;

	@Column(name = "user_region")
	private String region;

	@Column(name = "user_location")
	private String location;

	@Column(name = "user_designation")
	private String designation;

	@Enumerated(EnumType.STRING)
	private Status status = Status.ACTIVE;  // Default value is ACTIVE

	@Column(name = "user_timezone")
	private ZoneId timeZone;

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

    // ✅ New field to store Keycloak ID
    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;   // Keycloak ka userId store karenge taaki mapping rahe


    // ** Lifecycle Callback for Automatic Population **
	@PrePersist
	protected void onCreate() {
		// Automatically set current timestamp
		if (this.createdAt == null) {
			this.createdAt = new Date();
		}

		// Automatically set default timezone
		if (this.timeZone == null) {
			this.timeZone = ZoneId.systemDefault();
		}

		if (this.status == null) {
			this.status = Status.ACTIVE; // Default to ACTIVE before persisting
		}

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserMaster that = (UserMaster) o;
		return Objects.equals(userId, that.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId);
	}



}
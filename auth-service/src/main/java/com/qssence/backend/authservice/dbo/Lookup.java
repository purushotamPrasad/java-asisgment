package com.qssence.backend.authservice.dbo;

import java.util.Date;

import jakarta.persistence.Column;
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
@Table(name = "Lookup")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Lookup {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="lk_id")
	private Integer lkid;
	
	@Column(name = "lk_value")
	private String lkValue;
	
	@Column(name = "Long_name")
	private String Long_Name;
	
	@Column(name = "lk_set_name")
	private String lkSetName;
	
	@Column(name = "lk_rec_status")
	private Boolean lkrecStatus;
	
}

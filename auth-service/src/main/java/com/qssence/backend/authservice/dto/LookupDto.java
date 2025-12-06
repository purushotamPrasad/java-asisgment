package com.qssence.backend.authservice.dto;

import lombok.Data;

@Data
public class LookupDto {
	private Integer lkid;
	private String lkValue;
	private String LongName;
	private String lkSetName;
	private Boolean lkrecStatus;
}

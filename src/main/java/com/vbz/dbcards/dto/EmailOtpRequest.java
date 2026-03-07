package com.vbz.dbcards.dto;

import lombok.Data;

@Data
public class EmailOtpRequest {
	
	private Long mobileNumber;
	private String email;

}

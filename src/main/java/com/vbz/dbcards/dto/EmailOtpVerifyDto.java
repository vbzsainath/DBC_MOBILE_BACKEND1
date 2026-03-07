package com.vbz.dbcards.dto;

import lombok.Data;

@Data
public class EmailOtpVerifyDto {
	
	private String email;
	private String otp;

}

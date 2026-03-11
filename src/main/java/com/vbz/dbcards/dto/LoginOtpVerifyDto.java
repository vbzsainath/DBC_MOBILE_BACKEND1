package com.vbz.dbcards.dto;

import lombok.Data;

@Data
public class LoginOtpVerifyDto {
	
	public String email;
	public Long mobileNumber;
	public String otp;


}

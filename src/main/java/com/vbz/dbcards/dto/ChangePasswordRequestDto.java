package com.vbz.dbcards.dto;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {

	public String newPassword;
    public String confirmPassword;
}

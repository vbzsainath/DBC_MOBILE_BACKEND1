package com.vbz.dbcards.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    public String email;
    public String newPassword;
    public String confirmPassword;
}

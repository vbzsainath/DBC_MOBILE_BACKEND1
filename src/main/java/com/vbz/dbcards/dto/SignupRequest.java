package com.vbz.dbcards.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SignupRequest {
    public String firstName;
    public String middleName;
    public String lastName;
    public Long mobileNumber;
    public String email;
     
}

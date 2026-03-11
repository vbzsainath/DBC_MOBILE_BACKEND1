package com.vbz.dbcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class WebLoginRequest {

  @NotBlank(message = "Email is required")
  @Pattern(
      regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
      message = "Username must be a valid email address"
  )
  private String email;

  @NotBlank(message = "Password is required")
  private String password;
  
  public String getPassword() {
      return password;
  }  

  public void setPassword(String password) {
      this.password = password;
  }
}

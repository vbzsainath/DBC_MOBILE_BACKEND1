package com.vbz.dbcards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long userId;
	private LocalDateTime loginCooldown;

//  @Column(nullable = false, unique = true)
    private String firstName;
    
    private String middleName;
    
//  @Column(nullable = false, unique = true)
    private String lastName;
    
    @Column(name = "mobile_number", unique = true)
    private Long mobileNumber;
    
//  @Column(nullable = false, unique = true)
    private String email;
    
    private LocalDate dateOfBirth;
    
    private boolean numberVerified=false;
    
    private boolean emailVerified=false;
    
    private boolean userVerified=false;
     
    private String phoneOtp;
    
    private LocalDateTime phoneOtpExpiry;

    private String emailOtp;
    
    private LocalDateTime emailOtpExpiry;
    
    private String loginOtp;
    
    private LocalDateTime loginOtpExpiry;
    

    // Profile fields (for Save Profile UI)
    @Column(name = "name")
    private String name;
    
    // Audit columns
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "deleted_on")
    private LocalDateTime deletedOn;
    
    private String createBy;
    private String updatedBy; 
    private String deletedBy;
    
    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = LocalDateTime.now();
    }
    
   

}
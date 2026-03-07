package com.vbz.dbcards.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_ldap")
public class UserLdap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ldapId;

    @Email(message = "Username must be a valid email")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email; // Accepts e-mail only

    @NotBlank
    @Column(nullable = false)
    private String password;
    
    //  Audit columns
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "deleted_on")
    private LocalDateTime deletedOn;
    
    private Long createBy;
    private Long updatedBy;
    private Long deletedBy;
    public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Long getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(Long deletedBy) {
		this.deletedBy = deletedBy;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}

	@PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = LocalDateTime.now();
    }

   
    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    public String getPassword() { return password; }

   
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedOn() { return createdOn; }
    public LocalDateTime getUpdatedOn() { return updatedOn; }
    public LocalDateTime getDeletedOn() { return deletedOn; }

    public void setDeletedOn(LocalDateTime deletedOn) { this.deletedOn = deletedOn; }
    
   
}

package com.vbz.dbcards.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "password_reset")
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passwordResetId;

    @Column(nullable = false)
    private String username;

    @Column(name = "reset_pin", nullable = false)
    private String resetPin;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    
    private LocalDateTime updatedOn;
    private LocalDateTime deletedOn;
    
      private Long createBy;
      public Long getCreateBy() {
		return createBy;
	}
      

	public Long getPasswordResetId() {
		return passwordResetId;
	}


	public void setPasswordResetId(Long passwordResetId) {
		this.passwordResetId = passwordResetId;
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
	private Long updatedBy;
      private Long deletedBy;
    
    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getResetPin() {
        return resetPin;
    }

    public void setResetPin(String resetPin) {
        this.resetPin = resetPin;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

	

	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}

	

	public LocalDateTime getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(LocalDateTime deletedOn) {
		this.deletedOn = deletedOn;
	}

	
	@PrePersist
	protected void onCreate() {
	    this.createdOn = LocalDateTime.now();
	    
	}
	@PreUpdate
	protected void onUpdate() {
	    this.updatedOn = LocalDateTime.now();
	}
    
}

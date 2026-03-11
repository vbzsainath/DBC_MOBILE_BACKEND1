package com.vbz.dbcards.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class UserCardDtl {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ucdtl_id;
	
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@Column(name = "card_id", nullable = false)
	private Long cardId;

	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getCardId() {
		return cardId;
	}
	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
	@Column(nullable = false)
	private Boolean status;
	
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	private LocalDateTime createdOn;
	
	private Long createdBy;
	private LocalDateTime deletedOn;
	private Long deletedBy;
	private LocalDateTime updatedOn;
	private Long updatedBy;


	
	
	public Long getUcdtl_id() {
		return ucdtl_id;
	}
	public void setUcdtl_id(Long ucdtl_id) {
		this.ucdtl_id = ucdtl_id;
	}
	public LocalDateTime getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public LocalDateTime getDeletedOn() {
		return deletedOn;
	}
	public void setDeletedOn(LocalDateTime deletedOn) {
		this.deletedOn = deletedOn;
	}
	public Long getDeletedBy() {
		return deletedBy;
	}
	public void setDeletedBy(Long deletedBy) {
		this.deletedBy = deletedBy;
	}
	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}
	
	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}
	public Long getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}
	@PrePersist
	protected void onCreate() {
	    this.createdOn = LocalDateTime.now();
	    if(this.status==null) {
	    	this.status=true;
	    }
	    
	}
	@PreUpdate
	protected void onUpdate() {
	    this.updatedOn = LocalDateTime.now();
	}
	
}

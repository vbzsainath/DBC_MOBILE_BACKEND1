package com.vbz.dbcards.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;

@Entity
public class UserLogin {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userLoginId;
	
	@Column(name = "LILO_TS")
	private LocalDateTime LiLoTimeStamp;
	
	@Column(name="LILO_status")
	private Integer LiLoStatus;
	
	@Column(name="user_id")
	private Long userId;
	
	

	public Long getUserLoginId() {
		return userLoginId;
	}

	public void setUserLoginId(Long userLoginId) {
		this.userLoginId = userLoginId;
	}

	public LocalDateTime getLiLoTimeStamp() {
		return LiLoTimeStamp;
	}

	public void setLiLoTimeStamp(LocalDateTime liLoTimeStamp) {
		LiLoTimeStamp = liLoTimeStamp;
	}

	public Integer getLiLoStatus() {
		return LiLoStatus;
	}

	public void setLiLoStatus(Integer liLoStatus) {
		LiLoStatus = liLoStatus;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@PrePersist
	protected void onTimeStamp() {
		this.LiLoTimeStamp=LocalDateTime.now();
	}
	 
}

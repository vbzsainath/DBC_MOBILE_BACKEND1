package com.vbz.dbcards.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Templates {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long templateTableId;
	
	@Column(name = "template_id", nullable = false)
	private Long templateId;
	
	@Column(name = "template_slug", nullable = false)
	private String templateSlug;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
	@JsonBackReference
    private BusinessCard businessCard;

}

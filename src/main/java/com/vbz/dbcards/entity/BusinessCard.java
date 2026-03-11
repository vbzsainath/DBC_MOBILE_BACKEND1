package com.vbz.dbcards.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "business_cards")
@Getter
@Setter
public class BusinessCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @Column(name = "created_by", nullable = false)
    @JsonIgnore
    private Long createdBy;

    private String username;

    // Profile
    private String name;
    private String designation;
    private String companyName;
    private String phoneNumber;
    private String phoneNumber2;
    private String email;
    private String address;

    // Business
    private String keywords;
    private String businessCategory;
    private String businessSubcategory;
    private String clients;

    @Column(length = 2000)
    private String businessDescription;

    // Social
    private String linkedin;
    private String facebook;
    private String instagram;
    private String twitter;
    private String whatsappUrl;

    @Column(nullable = false)
    private String templateSlug;

    @Column(nullable = false)
    private Long templateId;

    // Files
    private String photo;
    private String logo;
    private String qrImage;
    private String qrImage2;
    private String businessPdf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Long updatedBy;

    //  SOFT DELETE FLAG
    @Column(nullable = false)
    private Boolean status = true;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
        // Null-safe defaults for NOT NULL columns.
        // Prevents DataIntegrityViolationException (→ 500) if frontend
        // fails to send templateSlug or templateId.
        if (this.templateSlug == null) this.templateSlug = "classic";
        if (this.templateId   == null) this.templateId   = 1L;
        if (this.status       == null) this.status        = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = LocalDateTime.now();
    }
}
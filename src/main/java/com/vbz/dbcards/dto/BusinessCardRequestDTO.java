package com.vbz.dbcards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BusinessCardRequestDTO {

    private String name;
    private String designation;
    private String companyName;
    private String phoneNumber;
    private String phoneNumber2;
    private String email;
    private String address;

    private String keywords;
    private String businessCategory;
    private String businessSubcategory;
    private String businessDescription;
    private String clients;

    private String linkedin;
    private String facebook;
    private String instagram;
    private String twitter;
    private String whatsappUrl;

    // S3 URLs
    private String photo;
    private String logo;
    private String qrImage;
    private String qrImage2;
    private String businessPdf;

    @NotNull(message="templateId is required")
    private Long templateId;

    @NotNull(message="templateSlug is required")
    private String templateSlug;
}
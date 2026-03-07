package com.vbz.dbcards.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BusinessCardResponse {

    private Long shareId;
    private String shareStatus;

    private Long cardId;
    private Long createdBy;
    private String username;

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
    private String clients;
    private String businessDescription;

    private String linkedin;
    private String facebook;
    private String instagram;
    private String twitter;
    private String whatsappUrl;

    private String templateSlug;
    private Long templateId;

    private String photo;
    private String logo;
    private String qrImage;
    private String qrImage2;
    private String businessPdf;
}
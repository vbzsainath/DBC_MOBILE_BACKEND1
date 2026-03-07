package com.vbz.dbcards.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class CardTemplateDTO {

    // ⭐ ADD THIS FIELD
    private Long cardId;

    private Long templateId;
    private String username;

    // template
    private String templateSlug;

    // basic profile
    private String name;
    private String designation;
    private String companyName;

    // contact
    @JsonAlias({"phoneNumber"})
    private String phoneNumber1;
    private String phoneNumber2;
    private String email;
    @JsonAlias({"address"})
    private String companyAddress;

    // business
    private String businessCategory;
    private String businessSubcategory;
    private String businessDescription;
    @JsonAlias({"clients"})
    private String clientList;

    // social
    private String linkedin;
    private String facebook;
    private String instagram;
    @JsonAlias({"twitter"})
    private String twitterXLink;
    private String whatsappUrl;

    // files
    private String profileImageFileId;
    private String logoFileId;
    private String qrFileId1;
    private String qrFileId2;
    private String pdfFileId;
}

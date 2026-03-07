package com.vbz.dbcards.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShareResponse {

    private String shareType;   // IN_APP / WHATSAPP
    private String message;
    private String whatsappUrl;
    private Long shareId;
}
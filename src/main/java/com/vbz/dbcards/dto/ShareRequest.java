package com.vbz.dbcards.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareRequest {

    private Long senderId;
    private Long receiverMobile;
    private Long cardId;
    private String shareMode; // IN_APP or WHATSAPP
}
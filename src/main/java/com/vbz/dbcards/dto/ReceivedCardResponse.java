package com.vbz.dbcards.dto;

import com.vbz.dbcards.entity.BusinessCard;
import com.vbz.dbcards.enums.ShareStatus;

public class ReceivedCardResponse {

    private Long sharedId;
    private ShareStatus status;
    private BusinessCard card;

    public ReceivedCardResponse(Long sharedId,
                                ShareStatus status,
                                BusinessCard card) {
        this.sharedId = sharedId;
        this.status = status;
        this.card = card;
    }

    public Long getSharedId() {
        return sharedId;
    }

    public ShareStatus getStatus() {
        return status;
    }

    public BusinessCard getCard() {
        return card;
    }
}
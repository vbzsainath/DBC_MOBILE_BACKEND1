package com.vbz.dbcards.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserCheckResponse {

    private boolean exists;
    private Long userId;
    private String message;
}
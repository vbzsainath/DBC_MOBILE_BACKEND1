package com.vbz.dbcards.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ShareTokenUtil {

    public static String generateToken(Long cardId, Long mobile) {

        String raw = cardId + ":" + mobile;

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Long extractCardId(String token) {

        String decoded = new String(
                Base64.getUrlDecoder().decode(token),
                StandardCharsets.UTF_8
        );

        String[] parts = decoded.split(":");

        return Long.parseLong(parts[0]);
    }
}
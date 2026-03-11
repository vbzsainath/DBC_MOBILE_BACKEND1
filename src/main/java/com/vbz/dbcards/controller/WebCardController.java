package com.vbz.dbcards.controller;

import com.vbz.dbcards.utils.ShareTokenUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebCardController {

    /**
     * Handles encrypted share link
     * Example:
     * https://sharecards.in/c/TOKEN
     */
    @GetMapping("/c/{token}")
    public ResponseEntity<?> resolveTokenLink(
            @PathVariable String token,
            @RequestParam(value = "ref", required = false) String refMobile) {

        Long cardId = ShareTokenUtil.extractCardId(token);

        String redirect = "/card/" + cardId;

        if (refMobile != null && !refMobile.isBlank()) {
            redirect += "?ref=" + refMobile;
        }

        return ResponseEntity
                .status(302)
                .header("Location", redirect)
                .build();
    }


    /**
     * Web preview page for non-DBC users
     */
    @GetMapping(value = "/card/{cardId}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> webCardPreview(
            @PathVariable String cardId,
            @RequestParam(value = "ref", required = false, defaultValue = "") String refMobile) {

        String html =
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                "</head>" +
                "<body>" +
                "<script>" +
                "window.location.href='http://18.61.15.41/dbc/dev/card/" + cardId + "';" +
                "</script>" +
                "</body>" +
                "</html>";

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
}
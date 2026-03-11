package com.vbz.dbcards.controller;

import com.vbz.dbcards.entity.BusinessCard;
import com.vbz.dbcards.entity.User;
import com.vbz.dbcards.exception.ResourceNotFoundException;
import com.vbz.dbcards.repository.BusinessCardRepository;
import com.vbz.dbcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CardController {

    private final BusinessCardRepository cardRepository;
    private final UserRepository         userRepository;

    /**
     * GET /cards/{cardId}
     * Legacy endpoint – returns the raw BusinessCard entity.
     */
    @GetMapping("/cards/{cardId}")
    public ResponseEntity<?> getCardById(@PathVariable Long cardId) {
        log.info("Get card by id: cardId={}", cardId);
        BusinessCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));
        return ResponseEntity.ok(card);
    }

    /**
     * GET /api/card/public/{cardId}
     *
     * Public endpoint – no session required.
     * Used by the SharedCardPreviewScreen (and any web page) to display
     * a shared card to a non-DBC user before they sign up.
     *
     * Returns a flat object with:
     *   card fields  +  senderFirstName  +  senderLastName  +  senderCompany
     */
    @GetMapping("/api/card/public/{cardId}")
    public ResponseEntity<?> getPublicCard(@PathVariable Long cardId) {

        log.info("Public card view: cardId={}", cardId);

        BusinessCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        // Build flat response
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("cardId",               card.getCardId());
        resp.put("name",                 card.getName());
        resp.put("designation",          card.getDesignation());
        resp.put("phoneNumber",          card.getPhoneNumber());
        resp.put("email",                card.getEmail());
        resp.put("address",              card.getAddress());
        resp.put("companyName",          card.getCompanyName());
        resp.put("businessCategory",     card.getBusinessCategory());
        resp.put("businessSubcategory",  card.getBusinessSubcategory());
        resp.put("businessDescription",  card.getBusinessDescription());
        resp.put("linkedin",             card.getLinkedin());
        resp.put("twitter",              card.getTwitter());
        resp.put("instagram",            card.getInstagram());
        resp.put("whatsappUrl",          card.getWhatsappUrl());
        resp.put("templateSlug",         card.getTemplateSlug() != null ? card.getTemplateSlug() : "classic");
        resp.put("photo",                card.getPhoto());
        resp.put("logo",                 card.getLogo());

        // Sender info
        if (card.getCreatedBy() != null) {
            userRepository.findById(card.getCreatedBy()).ifPresent(sender -> {
                String first  = sender.getFirstName()  != null ? sender.getFirstName()  : "";
                String last   = sender.getLastName()   != null ? sender.getLastName()   : "";
                resp.put("senderFirstName", first);
                resp.put("senderLastName",  last);
                resp.put("senderName",      (first + " " + last).trim());
            });
        }

        return ResponseEntity.ok(resp);
    }
}

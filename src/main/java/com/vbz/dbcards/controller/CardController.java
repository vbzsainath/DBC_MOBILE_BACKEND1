package com.vbz.dbcards.controller;

import com.vbz.dbcards.entity.BusinessCard;
import com.vbz.dbcards.exception.ResourceNotFoundException;
import com.vbz.dbcards.repository.BusinessCardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/cards")
public class CardController {

    private final BusinessCardRepository businessCardRepository;

    public CardController(BusinessCardRepository businessCardRepository) {
        this.businessCardRepository = businessCardRepository;
    }

    // GET /cards/{cardId}
    // 200 - card returned
    // 404 - card not found  (was RuntimeException -> 500)
    @GetMapping("/{cardId}")
    public ResponseEntity<?> getCardById(@PathVariable Long cardId) {

        log.info("Get card by id: cardId={}", cardId);

        BusinessCard card = businessCardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Card not found: cardId={}", cardId);
                    return new ResourceNotFoundException("Card not found with id: " + cardId);
                });

        return ResponseEntity.ok(card);
    }
}

package com.vbz.dbcards.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vbz.dbcards.dto.CardTemplateDTO;
import com.vbz.dbcards.entity.BusinessCard;
import com.vbz.dbcards.entity.User;
import com.vbz.dbcards.entity.UserCardDtl;
import com.vbz.dbcards.exception.ResourceNotFoundException;
import com.vbz.dbcards.exception.UnauthorizedException;
import com.vbz.dbcards.repository.BusinessCardRepository;
import com.vbz.dbcards.repository.UserCardDtlRepository;
import com.vbz.dbcards.repository.UserRepository;
import com.vbz.dbcards.service.BusinessCardService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class BusinessCardController {

    private static final Logger logger =
            LoggerFactory.getLogger(BusinessCardController.class);

    private final BusinessCardRepository repository;
    private final Validator validator;
    private final BusinessCardService cardService;
    private final UserCardDtlRepository cardDtlRepository;
    private final UserRepository userRepository;

    public BusinessCardController(
            BusinessCardRepository repository,
            Validator validator,
            BusinessCardService cardService,
            UserCardDtlRepository cardDtlRepository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.validator = validator;
        this.cardService = cardService;
        this.cardDtlRepository = cardDtlRepository;
        this.userRepository = userRepository;
    }

    // CREATE CARD (JSON REQUEST)
    @PostMapping(
            value = "/business-card",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createBusinessCard(
            @RequestBody CardTemplateDTO dto,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");
        String emailSession = (String) session.getAttribute("LOGGED_IN_USERNAME");

        logger.info("Create card request received. UserId: {}", userId);

        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", 0, "message", "User not logged in"));
        }

        try {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            BusinessCard card = new BusinessCard();

            card.setName(dto.getName());
            card.setDesignation(dto.getDesignation());
            card.setCompanyName(dto.getCompanyName());

            card.setPhoneNumber(dto.getPhoneNumber1());
            card.setPhoneNumber2(dto.getPhoneNumber2());
            card.setEmail(dto.getEmail());
            card.setAddress(dto.getCompanyAddress());

            card.setBusinessCategory(dto.getBusinessCategory());
            card.setBusinessSubcategory(dto.getBusinessSubcategory());
            card.setBusinessDescription(dto.getBusinessDescription());
            card.setClients(dto.getClientList());

            card.setLinkedin(dto.getLinkedin());
            card.setFacebook(dto.getFacebook());
            card.setInstagram(dto.getInstagram());
            card.setTwitter(dto.getTwitterXLink());
            card.setWhatsappUrl(dto.getWhatsappUrl());

            card.setTemplateSlug(dto.getTemplateSlug());
            card.setTemplateId(dto.getTemplateId());

            BusinessCard savedCard = repository.save(card);

            UserCardDtl userCardDtl = new UserCardDtl();
            userCardDtl.setCardId(savedCard.getCardId());
            userCardDtl.setUserId(userId);
            userCardDtl.setCreatedBy(userId);

            cardDtlRepository.save(userCardDtl);

            return ResponseEntity.ok(Map.of(
                    "status", 1,
                    "message", "Business card saved successfully",
                    "cardId", savedCard.getCardId()
            ));

        } catch (Exception e) {

            logger.error("Error creating card", e);

            return ResponseEntity.badRequest()
                    .body(Map.of("status", 0, "message", "Card creation failed"));
        }
    }

    // UPDATE CARD
    @PutMapping(value = "/update-card/{cardId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBusinessCard(
            @PathVariable Long cardId,
            @RequestBody Map<String, Object> dto,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");

        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "User not logged in"));
        }

        BusinessCard card = repository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        if (!card.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "You are not authorized to update this card"));
        }

        card.setUpdatedBy(userId);
        card.setUpdatedOn(LocalDateTime.now());

        repository.save(card);

        return ResponseEntity.ok(Map.of("message", "Card updated successfully"));
    }

    // VIEW CARDS
    @GetMapping("/view-cards")
    public List<CardTemplateDTO> viewCards(HttpSession session) {
        return cardService.getAllCards(session);
    }

    // DELETE CARD
    @DeleteMapping("/delete-card/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long cardId,
                                        HttpSession session) {

        cardService.deleteCard(cardId, session);
        return ResponseEntity.ok("Card deleted successfully");
    }
}
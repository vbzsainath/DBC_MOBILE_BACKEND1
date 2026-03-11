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

        logger.info("Create card request received. UserId: {}", userId);

        // Return 401 (not 400) so the frontend can detect session expiry
        // and redirect to login instead of showing a generic error toast.
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("status", 0, "message", "Session expired. Please login again."));
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

            // Null-safe defaults for NOT NULL columns.
            // If frontend sends null/missing values these prevent
            // DataIntegrityViolationException (→ 500 via GlobalExceptionHandler).
            card.setTemplateSlug(dto.getTemplateSlug() != null ? dto.getTemplateSlug() : "classic");
            card.setTemplateId(dto.getTemplateId()     != null ? dto.getTemplateId()   : 1L);

            card.setCreatedBy(userId);  // required: created_by NOT NULL
            card.setUser(user);          // required: links card to user for view-cards queries

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
            @RequestBody CardTemplateDTO dto,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");

        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("status", 0, "message", "Session expired. Please login again."));
        }

        BusinessCard card = repository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        if (!card.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "You are not authorized to update this card"));
        }

        // Apply only non-null fields — allows partial updates without wiping existing data
        if (dto.getName()                != null) card.setName(dto.getName());
        if (dto.getDesignation()         != null) card.setDesignation(dto.getDesignation());
        if (dto.getCompanyName()         != null) card.setCompanyName(dto.getCompanyName());
        if (dto.getPhoneNumber1()        != null) card.setPhoneNumber(dto.getPhoneNumber1());
        if (dto.getPhoneNumber2()        != null) card.setPhoneNumber2(dto.getPhoneNumber2());
        if (dto.getEmail()               != null) card.setEmail(dto.getEmail());
        if (dto.getCompanyAddress()      != null) card.setAddress(dto.getCompanyAddress());
        if (dto.getBusinessCategory()    != null) card.setBusinessCategory(dto.getBusinessCategory());
        if (dto.getBusinessSubcategory() != null) card.setBusinessSubcategory(dto.getBusinessSubcategory());
        if (dto.getBusinessDescription() != null) card.setBusinessDescription(dto.getBusinessDescription());
        if (dto.getClientList()          != null) card.setClients(dto.getClientList());
        if (dto.getLinkedin()            != null) card.setLinkedin(dto.getLinkedin());
        if (dto.getFacebook()            != null) card.setFacebook(dto.getFacebook());
        if (dto.getInstagram()           != null) card.setInstagram(dto.getInstagram());
        if (dto.getTwitterXLink()        != null) card.setTwitter(dto.getTwitterXLink());
        if (dto.getWhatsappUrl()         != null) card.setWhatsappUrl(dto.getWhatsappUrl());
        if (dto.getTemplateSlug()        != null) card.setTemplateSlug(dto.getTemplateSlug());
        if (dto.getTemplateId()          != null) card.setTemplateId(dto.getTemplateId());

        card.setUpdatedBy(userId);
        card.setUpdatedOn(LocalDateTime.now());
        repository.save(card);

        return ResponseEntity.ok(Map.of("status", 1, "message", "Card updated successfully", "cardId", cardId));
    }

    // VIEW MY OWN CARDS
    @GetMapping("/view-cards")
    public ResponseEntity<?> viewCards(HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("status", 0, "message", "Session expired. Please login again."));
        }
        return ResponseEntity.ok(cardService.getAllCards(session));
    }

    // VIEW SAVED (RECEIVED) CARDS — GET /api/cards/saved-cards
    @GetMapping("/saved-cards")
    public ResponseEntity<?> getSavedCards(HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("status", 0, "message", "Session expired. Please login again."));
        }

        List<com.vbz.dbcards.entity.UserCardDtl> savedRefs =
                cardDtlRepository.findByUserIdAndStatusTrue(userId);

        List<CardTemplateDTO> result = savedRefs.stream()
                .map(ref -> repository.findById(ref.getCardId()).orElse(null))
                .filter(card -> card != null && Boolean.TRUE.equals(card.getStatus()))
                .map(cardService::mapToDTO)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // DELETE CARD
    @DeleteMapping("/delete-card/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long cardId,
                                        HttpSession session) {

        cardService.deleteCard(cardId, session);
        return ResponseEntity.ok("Card deleted successfully");
    }
}
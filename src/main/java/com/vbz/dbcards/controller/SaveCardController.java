package com.vbz.dbcards.controller;

import com.vbz.dbcards.entity.UserCardDtl;
import com.vbz.dbcards.service.UserCardDtlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoint for saving a received business card to the user's personal collection.
 *
 * POST /api/save-card
 * Request body: { "userId": 5, "cardId": 12 }
 *
 * Responses:
 *   200 -> { "message": "Card saved successfully" }
 *   400 -> Missing required fields: userId and cardId are both required
 *   404 -> User not found / Card not found
 *   409 -> Card already saved by this user
 *   500 -> Unexpected server error only
 */
@Slf4j
@RestController
@RequestMapping("/api/save-card")
@RequiredArgsConstructor
public class SaveCardController {

    private final UserCardDtlService userCardDtlService;

    @PostMapping
    public ResponseEntity<?> saveCard(@RequestBody Map<String, Long> body) {

        Long userId = body.get("userId");
        Long cardId = body.get("cardId");

        // 400 - validate required fields
        if (userId == null || cardId == null) {
            log.warn("Save card request rejected - missing fields: userId={}, cardId={}",
                    userId, cardId);
            throw new IllegalArgumentException(
                    "Missing required fields: userId and cardId are both required");
        }

        userCardDtlService.saveCard(userId, cardId);

        return ResponseEntity.ok(Map.of("message", "Card saved successfully"));
    }
}

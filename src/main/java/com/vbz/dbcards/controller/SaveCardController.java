package com.vbz.dbcards.controller;

import com.vbz.dbcards.entity.UserCardDtl;
import com.vbz.dbcards.service.UserCardDtlService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * POST /api/save-card
 * Body: { "cardId": 12 }
 * userId is read from the server session — not from the request body.
 * This prevents any logged-in user from saving cards to another user's collection.
 */
@Slf4j
@RestController
@RequestMapping("/api/save-card")
@RequiredArgsConstructor
public class SaveCardController {

    private final UserCardDtlService userCardDtlService;

    @PostMapping
    public ResponseEntity<?> saveCard(
            @RequestBody Map<String, Long> body,
            HttpSession session) {

        // Get userId from session (not from body — security fix)
        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Session expired. Please login again."));
        }

        Long cardId = body.get("cardId");

        if (cardId == null) {
            log.warn("Save card request rejected - missing cardId for userId={}", userId);
            throw new IllegalArgumentException("Missing required field: cardId");
        }

        log.info("Save card: userId={} (from session), cardId={}", userId, cardId);
        userCardDtlService.saveCard(userId, cardId);

        return ResponseEntity.ok(Map.of("message", "Card saved successfully"));
    }
}

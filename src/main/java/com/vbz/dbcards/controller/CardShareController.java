package com.vbz.dbcards.controller;

import com.vbz.dbcards.dto.ShareRequest;
import com.vbz.dbcards.dto.UserCheckResponse;
import com.vbz.dbcards.service.CardShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class CardShareController {

    private final CardShareService shareService;

    // -----------------------------------------------------------------------
    // CHECK USER  GET /api/share/check-user?mobileNumber=...
    // -----------------------------------------------------------------------
    @GetMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestParam Long mobileNumber) {

        if (mobileNumber == null) {
            throw new IllegalArgumentException("mobileNumber is required");
        }

        return ResponseEntity.ok(shareService.checkUserByMobile(mobileNumber));
    }

    // -----------------------------------------------------------------------
    // IN-APP SHARE  POST /api/share
    // 200 - shared successfully
    // 400 - missing senderId / receiverMobile / cardId
    // 404 - card not found / receiver not found
    // 409 - already shared
    // -----------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<?> shareCard(@RequestBody ShareRequest request) {

        // Validate required fields -> 400
        if (request.getSenderId() == null
                || request.getReceiverMobile() == null
                || request.getCardId() == null) {

            log.warn("Share request rejected - missing required fields: senderId={}, " +
                            "receiverMobile={}, cardId={}",
                    request.getSenderId(), request.getReceiverMobile(), request.getCardId());

            throw new IllegalArgumentException(
                    "Missing required fields: senderId, receiverMobile, and cardId are all required");
        }

        return ResponseEntity.ok(
                shareService.shareCard(
                        request.getSenderId(),
                        request.getReceiverMobile(),
                        request.getCardId()
                )
        );
    }

    // -----------------------------------------------------------------------
    // RECEIVED CARDS  GET /api/share/received/{userId}
    // -----------------------------------------------------------------------
    @GetMapping("/received/{userId}")
    public ResponseEntity<?> getReceivedCards(@PathVariable Long userId) {

        return ResponseEntity.ok(shareService.getReceivedCards(userId));
    }

    // -----------------------------------------------------------------------
    // MARK VIEWED  PUT /api/share/view/{shareId}
    // -----------------------------------------------------------------------
    @PutMapping("/view/{shareId}")
    public ResponseEntity<?> markViewed(@PathVariable Long shareId) {

        return ResponseEntity.ok(shareService.markAsViewed(shareId));
    }

    // -----------------------------------------------------------------------
    // WHATSAPP SHARE  POST /api/share/whatsapp
    // -----------------------------------------------------------------------
    @PostMapping("/whatsapp")
    public ResponseEntity<?> shareViaWhatsApp(@RequestBody ShareRequest request) {

        if (request.getSenderId() == null
                || request.getReceiverMobile() == null
                || request.getCardId() == null) {

            throw new IllegalArgumentException(
                    "Missing required fields: senderId, receiverMobile, and cardId are all required");
        }

        return ResponseEntity.ok(
                shareService.generateWhatsAppShareLink(
                        request.getSenderId(),
                        request.getReceiverMobile(),
                        request.getCardId()
                )
        );
    }

    // -----------------------------------------------------------------------
    // UNSHARE  PUT /api/share/unshare/{shareId}?userId=...
    // 200 - unshared
    // 403 - userId is not the original sender
    // 404 - share record not found
    // -----------------------------------------------------------------------
    @PutMapping("/unshare/{shareId}")
    public ResponseEntity<?> unshareCard(
            @PathVariable Long shareId,
            @RequestParam Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        return ResponseEntity.ok(shareService.unshareCard(shareId, userId));
    }
}

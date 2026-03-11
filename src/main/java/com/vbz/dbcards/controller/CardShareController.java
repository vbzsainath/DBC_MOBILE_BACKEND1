package com.vbz.dbcards.controller;

import com.vbz.dbcards.dto.ShareRequest;
import com.vbz.dbcards.service.PendingShareService;
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
    private final PendingShareService pendingShareService;

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
    // RECEIVED CARDS  GET /api/share/received
    // userId comes from session — not URL — to prevent reading other users' inbox
    // -----------------------------------------------------------------------
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedCards(jakarta.servlet.http.HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(java.util.Map.of("message", "Session expired. Please login again."));
        }
        return ResponseEntity.ok(shareService.getReceivedCards(userId));
    }

    // -----------------------------------------------------------------------
    // RECEIVED CARD BY ID  GET /api/share/received/{shareId}
    // Backward-compatible endpoint — reads received cards from session, not URL.
    // The path variable is accepted but ignored; userId comes from session.
    // This prevents "No static resource" 500 errors from older app versions
    // that appended a userId or shareId to the URL.
    // -----------------------------------------------------------------------
    @GetMapping("/received/{shareId}")
    public ResponseEntity<?> getReceivedCardById(
            @PathVariable Long shareId,
            jakarta.servlet.http.HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(java.util.Map.of("message", "Session expired. Please login again."));
        }

        // Return the full inbox — client can filter by shareId if needed
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

    // -----------------------------------------------------------------------
    // GENERATE SHARE LINK  POST /api/share/generate-link
    // For non-DBC users: saves PendingShare, sends SMS+Email, returns links
    // Body: { cardId, recipientName, recipientMobile, recipientEmail? }
    // Responses: 200 shareLink+whatsappUrl+smsBody+emailSubject+emailBody
    //            400 missing fields  404 sender/card not found
    // -----------------------------------------------------------------------
    @PostMapping("/generate-link")
    public ResponseEntity<?> generateLink(
            @RequestBody java.util.Map<String, Object> body,
            jakarta.servlet.http.HttpSession session) {

        Long senderId = (Long) session.getAttribute("LOGGED_IN_USER_ID");
        if (senderId == null) {
            return ResponseEntity.status(401)
                    .body(java.util.Map.of("message", "Session expired. Please login again."));
        }

        Long cardId = body.get("cardId") instanceof Number ?
                ((Number) body.get("cardId")).longValue() : null;
        String recipientName   = (String) body.get("recipientName");
        Long recipientMobile   = body.get("recipientMobile") instanceof Number ?
                ((Number) body.get("recipientMobile")).longValue() : null;
        String recipientEmail  = (String) body.get("recipientEmail");

        if (cardId == null || recipientMobile == null
                || recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException(
                    "Required fields: cardId, recipientName, recipientMobile");
        }

        return ResponseEntity.ok(
                pendingShareService.generateInviteAndLink(
                        senderId, cardId,
                        recipientName.trim(), recipientMobile, recipientEmail));
    }
}

package com.vbz.dbcards.service;

import com.vbz.dbcards.dto.BusinessCardResponse;
import com.vbz.dbcards.dto.UserCheckResponse;
import com.vbz.dbcards.entity.*;
import com.vbz.dbcards.enums.ShareStatus;
import com.vbz.dbcards.exception.DuplicateResourceException;
import com.vbz.dbcards.exception.ResourceNotFoundException;
import com.vbz.dbcards.exception.UnauthorizedException;
import com.vbz.dbcards.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardShareService {

    private final UserRepository         userRepository;
    private final BusinessCardRepository businessCardRepository;
    private final DbcShareInfoRepository shareRepository;
    private final NotificationService    notificationService;

    // ── Check User ────────────────────────────────────────────────────────────
    /**
     * Returns user profile info so the frontend can show a preview card.
     * Frontend (ShareCardScreen) uses: exists, userId, firstName, lastName,
     * name, email, mobileNumber, profilePhoto.
     */
    public UserCheckResponse checkUserByMobile(Long mobileNumber) {
        return userRepository.findByMobileNumber(mobileNumber)
                .map(user -> {
                    // Try to get the sender's company from their first active business card
                    String photoUrl = null;
                    List<BusinessCard> userCards =
                            businessCardRepository.findByUserUserIdAndStatusTrue(user.getUserId());
                    if (!userCards.isEmpty()) {
                        photoUrl = userCards.get(0).getPhoto();
                    }

                    return UserCheckResponse.builder()
                            .exists(true)
                            .userId(user.getUserId())
                            .message("User Found")
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .name(user.getName())
                            .email(user.getEmail())
                            .mobileNumber(user.getMobileNumber())
                            .profilePhoto(photoUrl)
                            .build();
                })
                .orElse(UserCheckResponse.builder()
                        .exists(false)
                        .userId(null)
                        .message("User Not Found")
                        .build());
    }

    // ── In-App Share ──────────────────────────────────────────────────────────
    @Transactional
    public String shareCard(Long senderId, Long receiverMobile, Long cardId) {

        log.info("Share card request: sender={}, receiverMobile={}, cardId={}",
                senderId, receiverMobile, cardId);

        // 404 - receiver not registered
        User receiver = userRepository.findByMobileNumber(receiverMobile)
                .orElseThrow(() -> {
                    log.warn("Receiver not found: mobile={}", receiverMobile);
                    return new ResourceNotFoundException(
                            "User not registered with mobile: " + receiverMobile);
                });

        // 400 - cannot share with yourself
        if (receiver.getUserId().equals(senderId)) {
            log.warn("Self-share attempt: senderId={}", senderId);
            throw new IllegalArgumentException("Cannot share a card with yourself");
        }

        // 404 - card not found
        businessCardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Card not found: cardId={}", cardId);
                    return new ResourceNotFoundException("Card not found with id: " + cardId);
                });

        // 409 - already actively shared
        boolean alreadyShared = shareRepository
                .existsBySharedByUseridAndSharedToUseridAndSharedDbcIdAndStatuscodeTrue(
                        senderId, receiver.getUserId(), cardId);

        if (alreadyShared) {
            log.warn("Duplicate share: sender={}, receiver={}, cardId={}",
                    senderId, receiver.getUserId(), cardId);
            throw new DuplicateResourceException("Card already shared with this user");
        }

        // If previously unshared (statuscode=false), reactivate that row instead
        // of inserting — avoids DB unique-constraint crash on re-share after unshare
        DbcShareInfo share = shareRepository
                .findBySharedByUseridAndSharedToUseridAndSharedDbcId(
                        senderId, receiver.getUserId(), cardId)
                .orElse(new DbcShareInfo());

        share.setSharedByUserid(senderId);
        share.setSharedToUserid(receiver.getUserId());
        share.setSharedDbcId(cardId);
        share.setStatuscode(true);
        share.setShareStatus(ShareStatus.SENT);
        share.setDeletedBy(null);
        share.setDeletedOn(null);
        if (share.getCreatedBy() == null) share.setCreatedBy(senderId);
        share.setUpdatedBy(senderId);

        shareRepository.save(share);

        notificationService.sendNotification(
                senderId, receiver.getUserId(), "You received a new business card");

        log.info("Card shared successfully: sender={}, receiver={}, cardId={}",
                senderId, receiver.getUserId(), cardId);

        return "Card shared successfully";
    }

    // ── WhatsApp Share ────────────────────────────────────────────────────────
    /**
     * Returns a JSON object { whatsappUrl: "..." } so the frontend can
     * call Linking.openURL(data.whatsappUrl).
     *
     * Previously returned a plain String which broke the frontend check
     * for data?.whatsappUrl.
     */
    public Map<String, String> generateWhatsAppShareLink(
            Long senderId, Long receiverMobile, Long cardId) {

        log.info("WhatsApp share request: sender={}, receiverMobile={}, cardId={}",
                senderId, receiverMobile, cardId);

        // 404 - card not found
        businessCardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Card not found for WhatsApp share: cardId={}", cardId);
                    return new ResourceNotFoundException("Card not found with id: " + cardId);
                });

        String url = "https://wa.me/91" + receiverMobile
                + "?text=" + java.net.URLEncoder.encode(
                        "Check out my digital business card: https://sharecards.in/card/" + cardId,
                        java.nio.charset.StandardCharsets.UTF_8);

        return Map.of("whatsappUrl", url);
    }

    // ── Received Cards ────────────────────────────────────────────────────────
    /**
     * Returns inbox items with full sender info + nested card object.
     *
     * Previously missing: senderFirstName/LastName/Company, sharedAt,
     * viewedAt, and the nested card object — all of which InboxScreen needs.
     */
    @Transactional
    public List<BusinessCardResponse> getReceivedCards(Long userId) {

        log.info("Get received cards: userId={}", userId);

        // 404 - user not found
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: userId={}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        shareRepository.markAsDelivered(userId);

        List<DbcShareInfo> shares =
                shareRepository.findBySharedToUseridAndStatuscodeTrue(userId);

        return shares.stream()
                .map(share -> {
                    BusinessCard card =
                            businessCardRepository.findById(share.getSharedDbcId())
                                    .orElse(null);
                    if (card == null) return null;

                    // Look up sender's profile for display in InboxScreen
                    User sender = userRepository.findById(share.getSharedByUserid())
                            .orElse(null);

                    // Sender's company comes from their first active card
                    String senderCompany = null;
                    if (sender != null) {
                        List<BusinessCard> senderCards =
                                businessCardRepository.findByUserUserIdAndStatusTrue(
                                        sender.getUserId());
                        if (!senderCards.isEmpty()) {
                            senderCompany = senderCards.get(0).getCompanyName();
                        }
                    }

                    // viewedAt: non-null when card has been VIEWED
                    String viewedAt = null;
                    if (share.getShareStatus() == ShareStatus.VIEWED) {
                        LocalDateTime ts = share.getUpdatedOn() != null
                                ? share.getUpdatedOn()
                                : share.getCreatedOn();
                        viewedAt = ts != null ? ts.toString() : "";
                    }

                    return BusinessCardResponse.builder()
                            // ── Share metadata ──
                            .shareId(share.getDsid())
                            .shareStatus(share.getShareStatus().name())
                            .sharedAt(share.getCreatedOn() != null
                                    ? share.getCreatedOn().toString() : null)
                            .viewedAt(viewedAt)
                            // ── Sender info ──
                            .senderFirstName(sender != null ? sender.getFirstName() : null)
                            .senderLastName(sender != null ? sender.getLastName() : null)
                            .senderCompany(senderCompany)
                            // ── Nested card (passed to CardDetailsScreen) ──
                            .card(BusinessCardResponse.CardInfo.builder()
                                    .cardId(card.getCardId())
                                    .createdBy(card.getCreatedBy())
                                    .username(card.getUsername())
                                    .name(card.getName())
                                    .designation(card.getDesignation())
                                    .companyName(card.getCompanyName())
                                    .phoneNumber(card.getPhoneNumber())
                                    .phoneNumber2(card.getPhoneNumber2())
                                    .email(card.getEmail())
                                    .address(card.getAddress())
                                    .keywords(card.getKeywords())
                                    .businessCategory(card.getBusinessCategory())
                                    .businessSubcategory(card.getBusinessSubcategory())
                                    .clients(card.getClients())
                                    .businessDescription(card.getBusinessDescription())
                                    .linkedin(card.getLinkedin())
                                    .facebook(card.getFacebook())
                                    .instagram(card.getInstagram())
                                    .twitter(card.getTwitter())
                                    .whatsappUrl(card.getWhatsappUrl())
                                    .templateSlug(card.getTemplateSlug())
                                    .templateId(card.getTemplateId())
                                    .photo(card.getPhoto())
                                    .logo(card.getLogo())
                                    .qrImage(card.getQrImage())
                                    .qrImage2(card.getQrImage2())
                                    .businessPdf(card.getBusinessPdf())
                                    .build())
                            .build();
                })
                .filter(r -> r != null)
                .toList();
    }

    // ── Mark Viewed ───────────────────────────────────────────────────────────
    @Transactional
    public String markAsViewed(Long shareId) {

        log.info("Mark as viewed: shareId={}", shareId);

        DbcShareInfo share = shareRepository.findById(shareId)
                .orElseThrow(() -> {
                    log.warn("Share record not found: shareId={}", shareId);
                    return new ResourceNotFoundException(
                            "Share record not found with id: " + shareId);
                });

        share.setShareStatus(ShareStatus.VIEWED);
        shareRepository.save(share);
        return "Marked as VIEWED";
    }

    // ── Unshare ───────────────────────────────────────────────────────────────
    @Transactional
    public String unshareCard(Long shareId, Long userId) {

        log.info("Unshare request: shareId={}, userId={}", shareId, userId);

        DbcShareInfo share = shareRepository.findById(shareId)
                .orElseThrow(() -> {
                    log.warn("Share record not found: shareId={}", shareId);
                    return new ResourceNotFoundException(
                            "Share record not found with id: " + shareId);
                });

        if (!share.getSharedByUserid().equals(userId)) {
            log.warn("Unauthorized unshare: shareId={}, requestedBy={}, owner={}",
                    shareId, userId, share.getSharedByUserid());
            throw new UnauthorizedException("You are not authorized to unshare this card");
        }

        share.setStatuscode(false);
        share.setDeletedBy(userId);
        share.setDeletedOn(LocalDateTime.now());
        share.setUpdatedBy(userId);

        shareRepository.save(share);

        log.info("Card unshared: shareId={}, userId={}", shareId, userId);
        return "Card unshared successfully";
    }
}

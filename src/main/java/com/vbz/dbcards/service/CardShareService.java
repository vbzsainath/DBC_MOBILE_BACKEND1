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

@Slf4j
@Service
@RequiredArgsConstructor
public class CardShareService {

    private final UserRepository userRepository;
    private final BusinessCardRepository businessCardRepository;
    private final DbcShareInfoRepository shareRepository;
    private final NotificationService notificationService;

    // -----------------------------------------------------------------------
    // CHECK USER
    // -----------------------------------------------------------------------
    public UserCheckResponse checkUserByMobile(Long mobileNumber) {
        return userRepository.findByMobileNumber(mobileNumber)
                .map(user -> new UserCheckResponse(true, user.getUserId(), "User Found"))
                .orElse(new UserCheckResponse(false, null, "User Not Found"));
    }

    // -----------------------------------------------------------------------
    // IN-APP SHARE
    // -----------------------------------------------------------------------
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
        BusinessCard card = businessCardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Card not found: cardId={}", cardId);
                    return new ResourceNotFoundException("Card not found with id: " + cardId);
                });

        // 409 - duplicate share
        boolean alreadyShared = shareRepository
                .existsBySharedByUseridAndSharedToUseridAndSharedDbcIdAndStatuscodeTrue(
                        senderId, receiver.getUserId(), cardId);

        if (alreadyShared) {
            log.warn("Duplicate share: sender={}, receiver={}, cardId={}",
                    senderId, receiver.getUserId(), cardId);
            throw new DuplicateResourceException("Card already shared with this user");
        }

        DbcShareInfo share = new DbcShareInfo();
        share.setSharedByUserid(senderId);
        share.setSharedToUserid(receiver.getUserId());
        share.setSharedDbcId(cardId);
        share.setStatuscode(true);
        share.setShareStatus(ShareStatus.SENT);
        share.setCreatedBy(senderId);
        share.setUpdatedBy(senderId);

        shareRepository.save(share);

        notificationService.sendNotification(
                senderId, receiver.getUserId(), "You received a new business card");

        log.info("Card shared successfully: sender={}, receiver={}, cardId={}",
                senderId, receiver.getUserId(), cardId);

        return "Card shared successfully";
    }

    // -----------------------------------------------------------------------
    // WHATSAPP SHARE
    // -----------------------------------------------------------------------
    public String generateWhatsAppShareLink(Long senderId, Long receiverMobile, Long cardId) {

        log.info("WhatsApp share request: sender={}, receiverMobile={}, cardId={}",
                senderId, receiverMobile, cardId);

        // 404 - card not found
        businessCardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Card not found for WhatsApp share: cardId={}", cardId);
                    return new ResourceNotFoundException("Card not found with id: " + cardId);
                });

        return "https://wa.me/" + receiverMobile
                + "?text=Check my digital business card: "
                + "https://sharecards.in/card/" + cardId;
    }

    // -----------------------------------------------------------------------
    // RECEIVED CARDS
    // -----------------------------------------------------------------------
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
                .map(share -> businessCardRepository
                        .findById(share.getSharedDbcId())
                        .map(card ->
                                BusinessCardResponse.builder()
                                        .shareId(share.getDsid())
                                        .shareStatus(share.getShareStatus().name())
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
                                        .build()
                        )
                        .orElse(null)
                )
                .toList();
    }

    // -----------------------------------------------------------------------
    // MARK VIEWED
    // -----------------------------------------------------------------------
    @Transactional
    public String markAsViewed(Long shareId) {

        log.info("Mark as viewed: shareId={}", shareId);

        // 404 - share record not found
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

    // -----------------------------------------------------------------------
    // UNSHARE
    // -----------------------------------------------------------------------
    @Transactional
    public String unshareCard(Long shareId, Long userId) {

        log.info("Unshare request: shareId={}, userId={}", shareId, userId);

        // 404 - share record not found
        DbcShareInfo share = shareRepository.findById(shareId)
                .orElseThrow(() -> {
                    log.warn("Share record not found: shareId={}", shareId);
                    return new ResourceNotFoundException(
                            "Share record not found with id: " + shareId);
                });

        // 403 - not the original sender
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

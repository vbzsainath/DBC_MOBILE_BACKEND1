package com.vbz.dbcards.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vbz.dbcards.dto.CardTemplateDTO;
import com.vbz.dbcards.entity.BusinessCard;
import com.vbz.dbcards.entity.UserCardDtl;
import com.vbz.dbcards.entity.DbcShareInfo;
import com.vbz.dbcards.exception.ResourceNotFoundException;
import com.vbz.dbcards.exception.UnauthorizedException;
import com.vbz.dbcards.repository.BusinessCardRepository;
import com.vbz.dbcards.repository.UserCardDtlRepository;
import com.vbz.dbcards.repository.UserRepository;
import com.vbz.dbcards.repository.DbcShareInfoRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class BusinessCardService {

    private static final Logger logger =
            LoggerFactory.getLogger(BusinessCardService.class);

    private final BusinessCardRepository repository;
    private final UserRepository userRepository;
    private final UserCardDtlRepository cardDtlRepository;
    private final DbcShareInfoRepository shareRepository;
    private final NotificationService notificationService;

    public BusinessCardService(
            BusinessCardRepository repository,
            UserRepository userRepository,
            UserCardDtlRepository cardDtlRepository,
            DbcShareInfoRepository shareRepository,
            NotificationService notificationService
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.cardDtlRepository = cardDtlRepository;
        this.shareRepository = shareRepository;
        this.notificationService = notificationService;
    }

    public void deleteCard(Long cardId, HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");

        logger.info("User {} attempting to delete card {}", userId, cardId);

        // 400 - not logged in
        if (userId == null)
            throw new IllegalArgumentException("User not logged in");

        // 404 - card not found
        BusinessCard card = repository.findById(cardId)
                .orElseThrow(() -> {
                    logger.warn("Card not found: cardId={}", cardId);
                    return new ResourceNotFoundException("Card not found with id: " + cardId);
                });

        // 403 - not the card owner
        if (!card.getUser().getUserId().equals(userId)) {
            logger.warn("Unauthorized delete attempt: userId={}, cardId={}", userId, cardId);
            throw new UnauthorizedException("You are not authorized to delete this card");
        }

        card.setStatus(false);
        card.setUpdatedBy(userId);
        card.setUpdatedOn(LocalDateTime.now());
        repository.save(card);

        logger.info("Card {} soft deleted successfully", cardId);

        // 404 - UserCardDtl record not found (log warning but don't fail the delete)
        cardDtlRepository.findById(cardId).ifPresentOrElse(
                cardDtl -> {
                    cardDtl.setDeletedBy(userId);
                    cardDtl.setDeletedOn(LocalDateTime.now());
                    cardDtlRepository.save(cardDtl);
                },
                () -> logger.warn("UserCardDtl not found for cardId={} during delete", cardId)
        );
    }

    public List<CardTemplateDTO> getAllCards(HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");

        logger.info("Fetching cards for user {}", userId);

        // 400 - not logged in
        if (userId == null)
            throw new IllegalArgumentException("User not logged in");

        return repository.findByUserUserIdAndStatusTrue(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CardTemplateDTO mapToDTO(BusinessCard card) {
        CardTemplateDTO dto = new CardTemplateDTO();
        dto.setCardId(card.getCardId());
        dto.setTemplateId(card.getTemplateId());
        dto.setUsername(card.getUsername());
        dto.setTemplateSlug(card.getTemplateSlug());
        dto.setName(card.getName());
        dto.setDesignation(card.getDesignation());
        dto.setCompanyName(card.getCompanyName());
        dto.setPhoneNumber1(card.getPhoneNumber());
        dto.setPhoneNumber2(card.getPhoneNumber2());
        dto.setEmail(card.getEmail());
        dto.setCompanyAddress(card.getAddress());
        dto.setBusinessCategory(card.getBusinessCategory());
        dto.setBusinessSubcategory(card.getBusinessSubcategory());
        dto.setBusinessDescription(card.getBusinessDescription());
        dto.setClientList(card.getClients());
        dto.setLinkedin(card.getLinkedin());
        dto.setFacebook(card.getFacebook());
        dto.setInstagram(card.getInstagram());
        dto.setTwitterXLink(card.getTwitter());
        dto.setWhatsappUrl(card.getWhatsappUrl());
        dto.setProfileImageFileId(card.getPhoto());
        dto.setLogoFileId(card.getLogo());
        dto.setQrFileId1(card.getQrImage());
        dto.setQrFileId2(card.getQrImage2());
        dto.setPdfFileId(card.getBusinessPdf());
        return dto;
    }

    public void notifyReceiversCardUpdated(Long cardId, Long senderId) {
        List<DbcShareInfo> receivers =
                shareRepository.findBySharedDbcIdAndStatuscodeTrue(cardId);
        for (DbcShareInfo share : receivers) {
            notificationService.sendNotification(
                    senderId, share.getSharedToUserid(), "A shared business card was updated");
        }
    }
}

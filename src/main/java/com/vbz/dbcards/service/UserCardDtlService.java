package com.vbz.dbcards.service;

import com.vbz.dbcards.entity.BusinessCard;
import com.vbz.dbcards.entity.User;
import com.vbz.dbcards.entity.UserCardDtl;
import com.vbz.dbcards.exception.DuplicateResourceException;
import com.vbz.dbcards.exception.ResourceNotFoundException;
import com.vbz.dbcards.repository.BusinessCardRepository;
import com.vbz.dbcards.repository.UserCardDtlRepository;
import com.vbz.dbcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles saving a received business card to a user's personal collection.
 *
 * Error contract:
 *   ResourceNotFoundException  -> 404  user or card not found
 *   DuplicateResourceException -> 409  card already saved
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCardDtlService {

    private final UserCardDtlRepository repository;
    private final UserRepository userRepository;
    private final BusinessCardRepository cardRepository;

    /**
     * Save a card to the user's collection.
     *
     * @param userId  ID of the user saving the card
     * @param cardId  ID of the BusinessCard to save
     * @return the persisted UserCardDtl record
     */
    @Transactional
    public UserCardDtl saveCard(Long userId, Long cardId) {

        log.info("Save card request: userId={}, cardId={}", userId, cardId);

        // 404 - user not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: userId={}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        // 404 - card not found
        BusinessCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Card not found: cardId={}", cardId);
                    return new ResourceNotFoundException("Card not found with id: " + cardId);
                });

        // 409 - already saved
        boolean alreadySaved = repository.existsByUserIdAndCardIdAndStatusTrue(userId, cardId);
        if (alreadySaved) {
            log.warn("Duplicate save attempt: userId={}, cardId={}", userId, cardId);
            throw new DuplicateResourceException("Card already saved by this user");
        }

        UserCardDtl entity = new UserCardDtl();
        entity.setUserId(userId);
        entity.setCardId(cardId);
        entity.setCreatedBy(userId);

        UserCardDtl saved = repository.save(entity);

        log.info("Card saved successfully: userId={}, cardId={}", userId, cardId);
        return saved;
    }
}

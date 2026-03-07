package com.vbz.dbcards.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.vbz.dbcards.entity.BusinessCard;

public interface BusinessCardRepository extends JpaRepository<BusinessCard, Long> {

    Optional<BusinessCard> findByUsername(String username);

    Optional<BusinessCard> findById(Long cardId);

    //  Only active cards
    List<BusinessCard> findByUserUserIdAndStatusTrue(Long userId);
}
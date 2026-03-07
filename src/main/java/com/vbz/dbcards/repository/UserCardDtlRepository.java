package com.vbz.dbcards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vbz.dbcards.entity.UserCardDtl;

import java.util.List;

public interface UserCardDtlRepository extends JpaRepository<UserCardDtl, Long> {

    /**
     * Used by SaveCardService to detect duplicate saves before inserting (-> 409).
     */
    boolean existsByUserIdAndCardIdAndStatusTrue(Long userId, Long cardId);

    /**
     * Returns all active saved cards for a user.
     */
    List<UserCardDtl> findByUserIdAndStatusTrue(Long userId);
}

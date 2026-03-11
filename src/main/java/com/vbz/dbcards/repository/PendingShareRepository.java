package com.vbz.dbcards.repository;

import com.vbz.dbcards.entity.PendingShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingShareRepository extends JpaRepository<PendingShare, Long> {

    /** All unclaimed pending shares for this mobile number — used on login. */
    List<PendingShare> findByReceiverMobileAndClaimedFalse(Long receiverMobile);

    /** Avoid sending duplicate invitations for the same card to the same number. */
    boolean existsBySenderIdAndReceiverMobileAndCardId(
            Long senderId, Long receiverMobile, Long cardId);
}

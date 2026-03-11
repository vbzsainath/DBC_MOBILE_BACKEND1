package com.vbz.dbcards.repository;

import com.vbz.dbcards.entity.DbcShareInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DbcShareInfoRepository
        extends JpaRepository<DbcShareInfo, Long> {

    List<DbcShareInfo> findBySharedToUseridAndStatuscodeTrue(Long userId);

    boolean existsBySharedByUseridAndSharedToUseridAndSharedDbcIdAndStatuscodeTrue(
            Long sharedBy,
            Long sharedTo,
            Long cardId
    );

    @Modifying
    @Query("UPDATE DbcShareInfo d SET d.shareStatus = 'DELIVERED' " +
            "WHERE d.sharedToUserid = :userId AND d.shareStatus = 'SENT'")
    void markAsDelivered(Long userId);

    // ✅ NEW METHOD (for card update notifications)
    List<DbcShareInfo> findBySharedDbcIdAndStatuscodeTrue(Long cardId);

    /**
     * Used to reactivate a previously-unshared row instead of inserting a new one,
     * preventing unique-constraint violations on re-share after unshare.
     */
    java.util.Optional<DbcShareInfo> findBySharedByUseridAndSharedToUseridAndSharedDbcId(
            Long sharedByUserid, Long sharedToUserid, Long sharedDbcId);
}

package com.vbz.dbcards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Stores a card share intended for a mobile number that is not yet
 * registered on DBC. When the receiver registers/logs in for the
 * first time, all pending shares for their number are claimed and
 * converted into real DbcShareInfo records.
 */
@Entity
@Table(name = "pending_shares")
@Getter
@Setter
public class PendingShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(name = "receiver_mobile", nullable = false)
    private Long receiverMobile;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_email")
    private String receiverEmail;

    @Column(name = "claimed", nullable = false)
    private Boolean claimed = false;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
    }
}

package com.vbz.dbcards.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Returned by GET /api/share/received/{userId}
 *
 * InboxScreen renders:
 *   item.shareId            → key / mark-viewed API call
 *   item.shareStatus        → SENT / DELIVERED / VIEWED
 *   item.sharedAt           → relative time display
 *   item.viewedAt           → null = unread; non-null = read
 *   item.senderFirstName    → "John"
 *   item.senderLastName     → "Doe"
 *   item.senderCompany      → "Acme Inc"
 *   item.card               → nested card object passed to CardDetailsScreen
 *
 * CardDetailsScreen and shareVCard use item.card.*
 */
@Getter
@Builder
public class BusinessCardResponse {

    // ── Share metadata ───────────────────────────────────────────────────────
    private Long   shareId;
    private String shareStatus;
    private String sharedAt;    // ISO-8601 string ("2025-03-07T10:30:00")
    private String viewedAt;    // null = unread; set when share marked VIEWED

    // ── Sender info ──────────────────────────────────────────────────────────
    private String senderFirstName;
    private String senderLastName;
    private String senderCompany;   // from sender's first active business card

    // ── Nested card (passed to CardDetailsScreen and shareVCard) ─────────────
    private CardInfo card;

    // ── Nested card DTO ──────────────────────────────────────────────────────
    @Getter
    @Builder
    public static class CardInfo {
        private Long   cardId;
        private Long   createdBy;
        private String username;

        private String name;
        private String designation;
        private String companyName;
        private String phoneNumber;
        private String phoneNumber2;
        private String email;
        private String address;

        private String keywords;
        private String businessCategory;
        private String businessSubcategory;
        private String clients;
        private String businessDescription;

        private String linkedin;
        private String facebook;
        private String instagram;
        private String twitter;
        private String whatsappUrl;

        private String templateSlug;
        private Long   templateId;

        private String photo;
        private String logo;
        private String qrImage;
        private String qrImage2;
        private String businessPdf;
    }
}

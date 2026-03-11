package com.vbz.dbcards.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Returned by GET /api/share/check-user
 *
 * Frontend (ShareCardScreen) uses:
 *   data.exists          → show/hide share options
 *   data.userId          → sendNotification
 *   data.firstName/lastName/name → user preview card
 *   data.email / mobileNumber   → user preview card
 *   data.profilePhoto           → avatar (optional)
 */
@Getter
@Builder
public class UserCheckResponse {

    private boolean exists;
    private Long    userId;
    private String  message;

    // User profile fields shown in ShareCardScreen's "User Preview Card"
    private String firstName;
    private String lastName;
    private String name;           // full name if stored as single field
    private String email;
    private Long   mobileNumber;
    private String profilePhoto;   // S3 URL or null
}

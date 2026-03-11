package com.vbz.dbcards.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vbz.dbcards.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class ProfileController {

    private static final Logger logger =
            LoggerFactory.getLogger(ProfileController.class);

    private final UserService service;

    public ProfileController(UserService service) {
        this.service = service;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");

        if (userId == null) {
            logger.warn("Unauthorized profile access attempt — no session userId");
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "status", 0,
                            "message", "User not logged in"
                    ));
        }

        logger.info("Fetching profile for userId: {}", userId);
        return ResponseEntity.ok(service.getUserProfile(userId));
    }
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, Object> body,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");

        if (userId == null) {
            logger.warn("Unauthorized profile update attempt — no session userId");
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "status", 0,
                            "message", "User not logged in"
                    ));
        }

        logger.info("Updating profile for userId: {} with data: {}", userId, body);
        return ResponseEntity.ok(service.updateUserProfile(userId, body));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");

        logger.info("User logging out. userId: {}", userId);

        session.invalidate();

        return ResponseEntity.ok(
                Map.of(
                        "status", 1,
                        "message", "Logged out successfully"
                )
        );
    }
}

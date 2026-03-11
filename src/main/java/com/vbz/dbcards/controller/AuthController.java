package com.vbz.dbcards.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vbz.dbcards.dto.*;
import com.vbz.dbcards.service.AuthService;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // ================= LOGIN SEND OTP (USED FOR RESEND ALSO) =================
    @PostMapping("/mobile/login")
    public ResponseEntity<Map<String,Object>> loginRequest(
            @RequestBody LoginRequest dto) {

        logger.info("Login OTP request for mobile: {}",
                dto.getMobileNumber());

        Map<String,Object> res = service.mobileLogin(dto);

        if ((int) res.get("status") == 1) {
            return ResponseEntity.ok(res);
        }

        return ResponseEntity.badRequest().body(res);
    }

    // ================= LOGIN VERIFY OTP =================
    @PostMapping("/login/verify-otp")
    public ResponseEntity<Map<String, Object>> loginVerifyOtp(
            @RequestBody LoginOtpVerifyDto dto,
            HttpSession session) {

        logger.info("OTP verification attempt for mobile: {}",
                dto.getMobileNumber());

        Map<String, Object> res =
                service.loginVerifyOtp(dto, session);

        if ((int) res.get("status") == 1) {

            Long userId =
                    (Long) session.getAttribute("LOGGED_IN_USER_ID");

            logger.info("Login success. userId: {}, sessionId: {}",
                    userId, session.getId());

            // Include sessionId + userId in the response body so that mobile
            // clients (React Native / Expo) can manually persist the session
            // cookie via AsyncStorage, since Set-Cookie headers are not
            // accessible from the JS networking layer on mobile.
            Map<String, Object> enrichedRes = new HashMap<>(res);
            enrichedRes.put("sessionId", session.getId());
            enrichedRes.put("userId", userId);

            return ResponseEntity.ok(enrichedRes);
        }

        return ResponseEntity.badRequest().body(res);
    }

    // ================= LOGOUT =================
 // AFTER — also expire the JSESSIONID cookie
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpSession session,
            jakarta.servlet.http.HttpServletResponse response) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");

        logger.info("Logout: userId={}, sessionId={}", userId, session.getId()); // ← fixed

        session.invalidate();

        jakarta.servlet.http.Cookie cookie =
                new jakarta.servlet.http.Cookie("JSESSIONID", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("status", 1, "message", "Logged out successfully"));
    }
}
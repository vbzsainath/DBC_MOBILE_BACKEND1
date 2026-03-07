package com.vbz.dbcards.controller;

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

            return ResponseEntity.ok(res);
        }

        return ResponseEntity.badRequest().body(res);
    }

    // ================= LOGOUT =================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        Long userId =
                (Long) session.getAttribute("LOGGED_IN_USER_ID");

        logger.info("Logout request. userId: {}, sessionId: {}",
                userId, session.getId());

        session.invalidate();

        return ResponseEntity.ok(Map.of(
                "status", 1,
                "message", "Logged out successfully"
        ));
    }
}
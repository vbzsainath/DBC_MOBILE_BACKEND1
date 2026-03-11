package com.vbz.dbcards.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vbz.dbcards.dto.SignupRequest;
import com.vbz.dbcards.entity.User;
import com.vbz.dbcards.repository.UserRepository;
import com.vbz.dbcards.service.OtpService;
import com.vbz.dbcards.service.PendingShareService;

@RestController
@RequestMapping("/auth")
public class SignupController {

    private final UserRepository userRepository;
    private final OtpService otpService;

    private final PendingShareService pendingShareService;

    public SignupController(UserRepository userRepository,
                            OtpService otpService,
                            PendingShareService pendingShareService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.pendingShareService = pendingShareService;
    }

    @PostMapping("/mobile-signup")
    public ResponseEntity<?> sendPhoneOtp(
            @RequestBody Map<String, Object> body) {

        try {

            System.out.println("Request body: " + body);

            Long mobile = Long.valueOf(body.get("mobileNumber").toString());

            if (userRepository.existsByMobileNumber(mobile)) {
                return ResponseEntity.badRequest().body(
                        Map.of("status", 0,
                               "message", "User already exists"));
            }

            Map<String,Object> res =
                    otpService.sendPhoneOtp(mobile);

            if ((int)res.get("status") == 0)
                return ResponseEntity.badRequest().body(res);

            return ResponseEntity.ok(res);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError().body(
                    Map.of("status",0,"error",e.getMessage()));
        }
    }
    @PostMapping("/verify-phone-otp")
    public ResponseEntity<?> verifyPhoneOtp(
            @RequestBody Map<String, Object> body) {

        // Null-safe: body.get() returns null when key is absent
        // → .toString() was throwing NullPointerException (prod crash)
        Object mobileRaw = body.get("mobileNumber");
        Object otpRaw    = body.get("otp");

        if (mobileRaw == null || otpRaw == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", 0, "message", "mobileNumber and otp are required"));
        }

        Long mobile;
        try {
            mobile = Long.valueOf(mobileRaw.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", 0, "message", "mobileNumber must be a valid number"));
        }

        boolean valid = otpService.verifyPhoneOtp(mobile, otpRaw.toString());

        if (!valid) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", 0, "message", "Invalid or expired OTP"));
        }

        return ResponseEntity.ok(Map.of("status", 1, "message", "Phone Number verified"));
    }

    @PostMapping("/send-email-otp")
    public ResponseEntity<?> sendEmailOtp(
            @RequestBody Map<String, String> body) {

        String email = body.get("email");

        //  If email is null or blank → do nothing
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", 0,
                           "message", "Email is empty"));
        }

        //  If email already exists
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", 0,
                           "message", "User already exists"));
        }

        Map<String,Object> res =
                otpService.sendEmailOtp(email);

        if ((int)res.get("status") == 0)
            return ResponseEntity.badRequest().body(res);

        return ResponseEntity.ok(res);
    }
    @PostMapping("/verify-email-otp")
    public ResponseEntity<?> verifyEmailOtp(
            @RequestBody Map<String, String> body) {

        String email = body.get("email");
        String otp = body.get("otp");

        boolean valid =
                otpService.verifyEmailOtp(email, otp);

        if (!valid) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", 0,
                           "message", "Invalid or expired OTP"));
        }

        return ResponseEntity.ok(
                Map.of("status", 1,
                       "message", "Email verified"));
    }

    @PostMapping("/final-submit")
    public ResponseEntity<?> finalSubmit(
            @RequestBody SignupRequest req,
            HttpSession session) {

        if (!otpService.isPhoneVerified(req.getMobileNumber())) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", 0,
                           "message", "Phone Number not verified"));
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()) {

            if (!otpService.isEmailVerified(req.getEmail())) {
                return ResponseEntity.badRequest().body(
                        Map.of("status", 0,
                               "message", "Email not verified"));
            }

        }
        // Guard: existsByEmail(null) throws or mis-matches — check only when email present
        boolean emailTaken = req.getEmail() != null
                && !req.getEmail().isBlank()
                && userRepository.existsByEmail(req.getEmail());

        if (userRepository.existsByMobileNumber(req.getMobileNumber()) || emailTaken) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", 0, "message", "User already exists"));
        }

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setMiddleName(req.getMiddleName());
        user.setLastName(req.getLastName());
        user.setMobileNumber(req.getMobileNumber());
        user.setEmail(req.getEmail());
        user.setNumberVerified(true);
        user.setEmailVerified(req.getEmail() != null);

        userRepository.save(user);

        // Auto-login: set session so user goes straight to inbox after signup
        session.setAttribute("LOGGED_IN_USER_ID", user.getUserId());

        // Claim any pending shares for this mobile number immediately
        pendingShareService.claimPendingShares(user.getUserId(), user.getMobileNumber());

        otpService.clearPhone(req.getMobileNumber());
        if (req.getEmail() != null)
            otpService.clearEmail(req.getEmail());

        // Return sessionId so the mobile app can persist the session cookie —
        // same pattern as loginVerifyOtp; mobile cannot read Set-Cookie headers
        Map<String, Object> response = new HashMap<>();
        response.put("status",    1);
        response.put("message",   "Signup successful. Welcome to ShareCards!");
        response.put("userId",    user.getUserId());
        response.put("sessionId", session.getId());
        response.put("autoLogin", true);
        return ResponseEntity.ok(response);
    }
}
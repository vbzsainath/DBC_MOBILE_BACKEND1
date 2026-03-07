package com.vbz.dbcards.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vbz.dbcards.dto.SignupRequest;
import com.vbz.dbcards.entity.User;
import com.vbz.dbcards.repository.UserRepository;
import com.vbz.dbcards.service.OtpService;

@RestController
@RequestMapping("/auth")
public class SignupController {

    private final UserRepository userRepository;
    private final OtpService otpService;

    public SignupController(UserRepository userRepository,
                            OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
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

        Long mobile =
                Long.valueOf(body.get("mobileNumber").toString());
        String otp = body.get("otp").toString();

        boolean valid =
                otpService.verifyPhoneOtp(mobile, otp);

        if (!valid) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", 0,
                           "message", "Invalid or expired OTP"));
        }

        return ResponseEntity.ok(
                Map.of("status", 1,
                       "message", "Phone Number verified"));
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
            @RequestBody SignupRequest req) {

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
        if (userRepository.existsByMobileNumber(req.getMobileNumber())
            || userRepository.existsByEmail(req.getEmail())) {

            return ResponseEntity.badRequest().body(
                    Map.of("status", 0,
                           "message", "User already exists"));
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

        otpService.clearPhone(req.getMobileNumber());
        if (req.getEmail() != null)
            otpService.clearEmail(req.getEmail());

        return ResponseEntity.ok(
                Map.of("status", 1,
                       "message", "Signup successful",
                       "userId", user.getUserId()));
    }
}
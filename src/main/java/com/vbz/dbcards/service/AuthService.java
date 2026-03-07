package com.vbz.dbcards.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vbz.dbcards.dto.*;
import com.vbz.dbcards.entity.User;
import com.vbz.dbcards.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    @Autowired
    private OtpService otpService;

    @Autowired
    private SmsService smsService;

    private final UserRepository userRepo;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ================= SIGNUP PHONE OTP =================
    public Map<String,Object> sendSignupPhoneOtp(Long mobile){

        if(userRepo.existsByMobileNumber(mobile)){
            return Map.of("status",0,"message","User already exists");
        }

        return otpService.sendPhoneOtp(mobile);
    }

    public Map<String,Object> verifySignupPhoneOtp(Long mobile,String otp){

        boolean valid = otpService.verifyPhoneOtp(mobile, otp);

        if(!valid)
            return Map.of("status",0,"message","Invalid or expired OTP");

        return Map.of("status",1,"message","Phone verified");
    }

    // ================= SIGNUP EMAIL OTP =================
    public Map<String,Object> sendSignupEmailOtp(String email){

        if(userRepo.existsByEmail(email)){
            return Map.of("status",0,"message","User already exists");
        }

        return otpService.sendEmailOtp(email);
    }

    public Map<String,Object> verifySignupEmailOtp(String email,String otp){

        boolean valid = otpService.verifyEmailOtp(email, otp);

        if(!valid)
            return Map.of("status",0,"message","Invalid or expired OTP");

        return Map.of("status",1,"message","Email verified");
    }

    // ================= FINAL SIGNUP =================
    public Map<String,Object> finalSignupSubmit(SignupRequest req){

        if(!otpService.isPhoneVerified(req.getMobileNumber()))
            return Map.of("status",0,"message","Phone not verified");

        if(req.getEmail()!=null &&
           !otpService.isEmailVerified(req.getEmail()))
            return Map.of("status",0,"message","Email not verified");

        if(userRepo.existsByMobileNumber(req.getMobileNumber())
           || userRepo.existsByEmail(req.getEmail()))
            return Map.of("status",0,"message","User already exists");

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setMiddleName(req.getMiddleName());
        user.setLastName(req.getLastName());
        user.setMobileNumber(req.getMobileNumber());
        user.setEmail(req.getEmail());
        user.setNumberVerified(true);
        user.setEmailVerified(true);

        userRepo.save(user);

        otpService.clearPhone(req.getMobileNumber());
        otpService.clearEmail(req.getEmail());

        return Map.of(
                "status",1,
                "message","Signup successful",
                "userId",user.getUserId());
    }

    // ================= LOGIN SEND OTP =================
    public Map<String,Object> mobileLogin(LoginRequest dto){

        User user = userRepo.findByMobileNumber(dto.getMobileNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getLoginCooldown()!=null &&
           user.getLoginCooldown().isAfter(LocalDateTime.now()))
            return Map.of(
                    "status",0,
                    "message","Wait 30 seconds before requesting again"
            );

        // Generate OTP
        String otp = String.valueOf(
                100000 + new SecureRandom().nextInt(900000));

        // Save OTP in DB
        user.setLoginOtp(otp);
        user.setLoginOtpExpiry(LocalDateTime.now().plusMinutes(5));
        user.setLoginCooldown(LocalDateTime.now().plusSeconds(30));

        userRepo.save(user);

        // Send OTP via SMS
        smsService.sendOtpSms(dto.getMobileNumber(), otp);

        System.out.println("Login OTP generated: " + otp);

        return Map.of(
                "status",1,
                "message","OTP sent successfully"
        );
    }

    // ================= LOGIN VERIFY =================
    public Map<String,Object> loginVerifyOtp(
            LoginOtpVerifyDto dto,
            HttpSession session){

        User user = userRepo.findByMobileNumber(dto.getMobileNumber())
                .orElse(null);

        if(user==null ||
           user.getLoginOtpExpiry()==null ||
           user.getLoginOtpExpiry().isBefore(LocalDateTime.now()) ||
           !user.getLoginOtp().equals(dto.getOtp()))
            return Map.of("status",0,"message","Invalid or expired OTP");

        user.setLoginOtp(null);
        user.setLoginOtpExpiry(null);
        userRepo.save(user);

        session.setAttribute("LOGGED_IN_USER_ID",user.getUserId());

        return Map.of("status",1,"message","Login successful");
    }
}
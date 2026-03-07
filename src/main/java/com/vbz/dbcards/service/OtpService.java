package com.vbz.dbcards.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class OtpService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    // SMS toggle (true = send SMS, false = console OTP)
    @Value("${otp.sms.enabled:false}")
    private boolean smsEnabled;

    private final SecureRandom random = new SecureRandom();

    private final Map<Long, String> phoneOtpStore = new ConcurrentHashMap<>();
    private final Map<Long, LocalDateTime> phoneOtpExpiry = new ConcurrentHashMap<>();
    private final Map<Long, LocalDateTime> phoneCooldown = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> phoneVerified = new ConcurrentHashMap<>();

    private final Map<String, String> emailOtpStore = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> emailOtpExpiry = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> emailCooldown = new ConcurrentHashMap<>();
    private final Map<String, Boolean> emailVerified = new ConcurrentHashMap<>();

    private String generateOtp() {
        return String.valueOf(100000 + random.nextInt(900000));
    }

    // ================= PHONE OTP =================
    public Map<String,Object> sendPhoneOtp(Long mobile){

        Map<String,Object> res = new HashMap<>();

        if(phoneCooldown.containsKey(mobile) &&
           phoneCooldown.get(mobile).isAfter(LocalDateTime.now())){

            res.put("status",0);
            res.put("message","Wait 30 seconds before requesting again");
            return res;
        }

        String otp = generateOtp();

        phoneOtpStore.put(mobile, otp);
        phoneOtpExpiry.put(mobile, LocalDateTime.now().plusMinutes(5));
        phoneCooldown.put(mobile, LocalDateTime.now().plusSeconds(30));

        System.out.println("Phone OTP generated: " + otp);

        if(smsEnabled){
            try {
                smsService.sendOtpSms(mobile, otp);
            } catch (Exception e) {
                System.out.println("SMS sending failed");
                e.printStackTrace();
            }
        } else {
            System.out.println("DEV MODE OTP for " + mobile + " : " + otp);
        }

        res.put("status",1);
        res.put("message","OTP sent successfully");

        return res;
    }

    // ================= LOGIN OTP =================
    public Map<String,Object> sendLoginOtp(Long mobile){

        Map<String,Object> res = new HashMap<>();

        if(phoneCooldown.containsKey(mobile) &&
           phoneCooldown.get(mobile).isAfter(LocalDateTime.now())){

            res.put("status",0);
            res.put("message","Wait 30 seconds before requesting again");
            return res;
        }

        String otp = generateOtp();

        phoneOtpStore.put(mobile, otp);
        phoneOtpExpiry.put(mobile, LocalDateTime.now().plusMinutes(5));
        phoneCooldown.put(mobile, LocalDateTime.now().plusSeconds(30));

        System.out.println("Login OTP generated: " + otp);

        if(smsEnabled){
            try {
                smsService.sendOtpSms(mobile, otp);
            } catch (Exception e) {
                System.out.println("SMS sending failed");
                e.printStackTrace();
            }
        } else {
            System.out.println("DEV MODE LOGIN OTP for " + mobile + " : " + otp);
        }

        res.put("status",1);
        res.put("message","OTP sent successfully");

        return res;
    }

    public boolean verifyPhoneOtp(Long mobile, String enteredOtp){

        if(!phoneOtpStore.containsKey(mobile))
            return false;

        if(phoneOtpExpiry.get(mobile).isBefore(LocalDateTime.now()))
            return false;

        if(!phoneOtpStore.get(mobile).equals(enteredOtp))
            return false;

        phoneVerified.put(mobile,true);

        phoneOtpStore.remove(mobile);
        phoneOtpExpiry.remove(mobile);

        return true;
    }

    public boolean isPhoneVerified(Long mobile){
        return phoneVerified.getOrDefault(mobile,false);
    }

    public void clearPhone(Long mobile){
        phoneVerified.remove(mobile);
    }

    // ================= EMAIL OTP =================
    public Map<String,Object> sendEmailOtp(String email){

        Map<String,Object> res = new HashMap<>();

        if(emailCooldown.containsKey(email) &&
           emailCooldown.get(email).isAfter(LocalDateTime.now())){

            res.put("status",0);
            res.put("message","Wait 30 seconds before requesting again");
            return res;
        }

        String otp = generateOtp();

        emailOtpStore.put(email, otp);
        emailOtpExpiry.put(email, LocalDateTime.now().plusMinutes(5));
        emailCooldown.put(email, LocalDateTime.now().plusSeconds(30));

        System.out.println("Signup Email OTP (Console): " + otp);

        emailService.sendEmailOtp(email, otp);

        res.put("status",1);
        res.put("message","OTP sent successfully");

        return res;
    }

    public boolean verifyEmailOtp(String email, String enteredOtp){

        if(!emailOtpStore.containsKey(email))
            return false;

        if(emailOtpExpiry.get(email).isBefore(LocalDateTime.now()))
            return false;

        if(!emailOtpStore.get(email).equals(enteredOtp))
            return false;

        emailVerified.put(email,true);

        emailOtpStore.remove(email);
        emailOtpExpiry.remove(email);

        return true;
    }

    public boolean isEmailVerified(String email){
        return emailVerified.getOrDefault(email,false);
    }

    public void clearEmail(String email){
        emailVerified.remove(email);
    }
}
package com.vbz.dbcards.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** Sends an OTP verification email during signup/login */
    public void sendEmailOtp(String toEmail, String otp) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("koushalyashivapuje@gmail.com");
        msg.setTo(toEmail);
        msg.setSubject("Email Verification OTP - DBCards");
        msg.setText("Your verification OTP is: " + otp + "\n\nValid for 5 minutes.");
        mailSender.send(msg);
    }

    /**
     * Sends an invitation email to a non-DBC user telling them a card
     * has been shared with them and how to access it.
     */
    public void sendInviteEmail(String toEmail, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("koushalyashivapuje@gmail.com");
        msg.setTo(toEmail);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}

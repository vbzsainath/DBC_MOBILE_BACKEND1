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

    public void sendEmailOtp(String toEmail, String otp) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("koushalyashivapuje@gmail.com");
        msg.setTo(toEmail);
        msg.setSubject("Email Verification OTP - DBCards");

        msg.setText(
        	    "Your verification OTP is: " + otp +
        	    "\n\nValid for 5 minutes."
        	);

        mailSender.send(msg);
    }
}

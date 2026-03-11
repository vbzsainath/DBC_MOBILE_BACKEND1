package com.vbz.dbcards.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsService {

    @Value("${sms.api-id}")
    private String apiId;

    @Value("${sms.api-password}")
    private String apiPassword;

    @Value("${sms.sender}")
    private String sender;

    @Value("${sms.template-id}")
    private String templateId;

    @Value("${sms.base-url}")
    private String baseUrl;

    @Value("${otp.sms.enabled:false}")
    private boolean smsEnabled;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtpSms(Long mobile, String otp) {

        if(!smsEnabled){
            System.out.println("DEV MODE OTP for " + mobile + " : " + otp);
            return;
        }

        try {

            String message =
                    "Your ShareCards OTP is " + otp +
                    ". Do not share it with anyone. Thank you. VENTBD";

            String encodedMessage =
                    URLEncoder.encode(message, StandardCharsets.UTF_8);

            String url =
                    baseUrl
                    + "?api_id=" + apiId
                    + "&api_password=" + apiPassword
                    + "&sender=" + sender
                    + "&number=91" + mobile
                    + "&message=" + encodedMessage
                    + "&template_id=" + templateId
                    + "&sms_type=transactional"
                    + "&sms_encoding=text";

            String response = restTemplate.getForObject(url, String.class);

            System.out.println("SMS API Response: " + response);

        } catch (Exception e) {

            System.out.println("SMS sending failed");
            e.printStackTrace();
        }
    }

    /**
     * Sends a plain-text invitation SMS to a non-DBC user.
     */
    public void sendInviteSms(Long mobile, String message) {

        if (!smsEnabled) {
            System.out.println("DEV MODE Invite SMS for " + mobile + ": " + message);
            return;
        }

        try {
            String encodedMessage =
                    java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);

            String url = baseUrl
                    + "?api_id=" + apiId
                    + "&api_password=" + apiPassword
                    + "&sender=" + sender
                    + "&number=91" + mobile
                    + "&message=" + encodedMessage
                    + "&template_id=" + templateId
                    + "&sms_type=transactional"
                    + "&sms_encoding=text";

            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Invite SMS Response: " + response);

        } catch (Exception e) {
            System.out.println("Invite SMS failed: " + e.getMessage());
        }
    }
}

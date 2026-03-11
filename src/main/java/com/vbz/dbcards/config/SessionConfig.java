package com.vbz.dbcards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Configures the JSESSIONID cookie so that:
 *  - React Native / mobile clients can forward it via the Cookie header
 *  - SameSite=None allows cross-site requests (Expo dev / production mobile)
 *  - HttpOnly=true prevents JS from reading the cookie directly (security)
 *
 * Works alongside spring-session-jdbc which persists the session data
 * in MySQL — so sessions survive server restarts.
 */
@Configuration
public class SessionConfig {

    @Bean
    public DefaultCookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        // Allow the cookie to be sent cross-site (required for Expo / React Native)
        serializer.setSameSite("None");
        // Must be true when SameSite=None; set false only for local HTTP dev
        serializer.setUseSecureCookie(false); // flip to true if HTTPS-only
        serializer.setCookieName("JSESSIONID");
        serializer.setUseHttpOnlyCookie(true);
        // 7 days in seconds
        serializer.setCookieMaxAge(7 * 24 * 60 * 60);
        return serializer;
    }
}

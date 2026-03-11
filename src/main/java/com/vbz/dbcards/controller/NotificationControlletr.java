package com.vbz.dbcards.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.vbz.dbcards.entity.Notification;
import com.vbz.dbcards.repository.NotificationRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class NotificationControlletr {

    private final NotificationRepository repository;

    public NotificationControlletr(NotificationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Notification> getNotifications(HttpSession session) {

        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");

        if (userId == null) {
            throw new IllegalArgumentException("User not logged in");
        }

        return repository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }
}
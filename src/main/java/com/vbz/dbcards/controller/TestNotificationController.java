package com.vbz.dbcards.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vbz.dbcards.service.NotificationService;

@RestController
		@RequestMapping("/test")
		public class TestNotificationController {

		    private final NotificationService notificationService;

		    public TestNotificationController(NotificationService notificationService) {
		        this.notificationService = notificationService;
		    }

		    @GetMapping("/notify")
		    public String sendTestNotification() {

		        notificationService.sendNotification(
		                1L,
		                1L,
		                "Test notification from backend"
		        );

		        return "Notification sent";
		    }
		}
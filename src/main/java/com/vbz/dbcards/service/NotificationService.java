package com.vbz.dbcards.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.vbz.dbcards.entity.Notification;
import com.vbz.dbcards.repository.NotificationRepository;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    public NotificationService(
            SimpMessagingTemplate messagingTemplate,
            NotificationRepository notificationRepository) {

        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
    }

    public void sendNotification(Long senderId, Long receiverId, String message) {

        Notification notification = new Notification();
        notification.setSenderId(senderId);
        notification.setReceiverId(receiverId);
        notification.setMessage(message);

        notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/cards",
                message
        );
    }
}
package com.vbz.dbcards.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vbz.dbcards.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
}
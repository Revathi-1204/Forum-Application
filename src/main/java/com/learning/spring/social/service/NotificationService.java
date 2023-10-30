package com.learning.spring.social.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learning.spring.social.entities.Notification;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.User;
import com.learning.spring.social.repositories.NotificationRepository;


@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void createNotification(User user, Post post, String notificationType, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setPost(post);
        notification.setNotificationType(notificationType);
        notification.setMessage(message);   
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
}
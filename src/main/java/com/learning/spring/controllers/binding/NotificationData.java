package com.learning.spring.controllers.binding;

import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.User;

import lombok.Data;

@Data
public class NotificationData {
    private User user;
    private Post post;
    private String eventType;
    private String message;
    
}
package com.example.notificationservice.service.channel;

import com.example.notificationservice.event.NotificationEvent;

public interface NotificationChannel {
    void send(NotificationEvent event, String subject, String htmlContent);
    boolean supports(com.example.notificationservice.enums.NotificationChannel channel);
}
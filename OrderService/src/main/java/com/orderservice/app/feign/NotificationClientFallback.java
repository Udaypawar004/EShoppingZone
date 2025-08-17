package com.orderservice.app.feign;

import com.orderservice.app.dto.NotificationDetailsWithAttachDTO;
import com.orderservice.app.dto.NotificationDetailsDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback implements NotificationClient {
   @Override
    public String sendEmail(NotificationDetailsDTO notificationDetailsDTO) {
         return "Notification service is not available";
    }

    @Override
    public String sendMailWithAttachment(NotificationDetailsWithAttachDTO notificationDetails) {
        return "Email can't be send with attachment";
    }
}

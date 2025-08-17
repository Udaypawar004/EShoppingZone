package com.userservice.feign;

import com.userservice.dto.NotificationDetailsDTO;
import com.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback implements NotificationClient {
    @Override
    public String sendRegistrationEmail(User to, String token) {
        // Handle the fallback logic
        return "Failed to send email. Please try again later.";
    }

    @Override
    public String sendEmailResponse(NotificationDetailsDTO details) {
        return "Failed to send Email.";
    }
}

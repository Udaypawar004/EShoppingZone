package com.notificationservice.service;

import com.notificationservice.dto.NotificationDetails;
import com.notificationservice.dto.NotificationDetailsWithAttach;
import com.notificationservice.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    public ResponseEntity<?> sendMail(NotificationDetails details);

    public ResponseEntity<?> registerUser(UserDto user, String password);

    public ResponseEntity<?> sendMailWithAttachment(NotificationDetailsWithAttach details);
}

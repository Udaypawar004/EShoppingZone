package com.notificationservice;

import com.notificationservice.dto.NotificationDetails;
import com.notificationservice.dto.UserDto;
import com.notificationservice.service.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @InjectMocks
    private NotificationServiceImpl emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void shouldSendNotificationEmailSuccessfully() {
        NotificationDetails details = new NotificationDetails();
        details.setRecipient("user@example.com");
        details.setSubject("Hello");
        details.setMsgBody("This is a test email");

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        ResponseEntity<?> response = emailService.sendMail(details);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    void shouldSendVerificationEmailSuccessfully() {
        String email = "newuser@example.com";
        String password = "securePass123";
        UserDto user = new UserDto();
        user.setEmailId(email);

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        ResponseEntity<?> response = emailService.registerUser(user, password);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Verification email sent successfully", response.getBody());
    }

    @Test
    void shouldFailToSendNotificationEmailWhenExceptionThrown() {
        NotificationDetails details = new NotificationDetails();
        details.setRecipient("fail@example.com");
        details.setSubject("Fails");
        details.setMsgBody("This will fail");

        doThrow(new MailSendException("Mail server error")).when(javaMailSender).send(any(SimpleMailMessage.class));

        Exception exception = assertThrows(MailSendException.class, () ->
                emailService.sendMail(details)
        );

        assertEquals("Mail server error", exception.getMessage());
    }

    @Test
    void shouldFailToSendVerificationEmailWhenRecipientInvalid() {
        String password = "password";
        UserDto user = new UserDto();
        user.setEmailId("");  // or malformed email

        doThrow(new MailSendException("Invalid recipient")).when(javaMailSender).send(any(SimpleMailMessage.class));

        assertThrows(MailSendException.class, () -> emailService.registerUser(user, password));
    }

}

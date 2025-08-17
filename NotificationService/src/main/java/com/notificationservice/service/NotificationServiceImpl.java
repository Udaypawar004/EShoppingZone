package com.notificationservice.service;


import com.notificationservice.dto.NotificationDetails;
import com.notificationservice.dto.NotificationDetailsWithAttach;
import com.notificationservice.dto.UserDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    SimpleMailMessage mailMessage = new SimpleMailMessage();

    @Override
    public ResponseEntity<?> sendMail(NotificationDetails details) {
        mailMessage.setFrom(sender);
        mailMessage.setTo(details.getRecipient());
        mailMessage.setSubject(details.getSubject());
        mailMessage.setText(details.getMsgBody());

        javaMailSender.send(mailMessage);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> sendMailWithAttachment(NotificationDetailsWithAttach details) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            System.out.println("Recipient: " + details.getRecipient() + "\nSubject: " + details.getSubject());

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());
            helper.setText(details.getMsgBody());
            helper.addAttachment("Invoice.pdf", new ByteArrayResource(details.getPdfBytes()));
            javaMailSender.send(message);

        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> registerUser(UserDto email, String password) {
        String subject = "Verify your email";
        String body = "Dear " + email.getFullName() + ",\n\nUse this password below:\n" + password + "\n\n for login \n\nThank you!";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setTo(email.getEmailId());
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        javaMailSender.send(mailMessage);
        return new ResponseEntity<>("Verification email sent successfully", HttpStatus.OK);
    }
}

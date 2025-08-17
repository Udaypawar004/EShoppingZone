package com.orderservice.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class NotificationDetailsWithAttachDTO {
    private String recipient;

    private String subject;

    private String msgBody;

    private byte[] pdfBytes;
}
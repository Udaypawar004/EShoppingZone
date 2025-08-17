package com.orderservice.app.feign;

import com.orderservice.app.dto.NotificationDetailsDTO;
import com.orderservice.app.dto.NotificationDetailsWithAttachDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATIONSERVICE",url="http://localhost:8003/")
public interface NotificationClient {

    @PostMapping("/notification/sendMail")
    String sendEmail(@RequestBody NotificationDetailsDTO notificationDetailsDTO);

    @PostMapping("/notification/sendMailWithAttachment")
    String sendMailWithAttachment(@RequestBody NotificationDetailsWithAttachDTO notificationDetails);
}

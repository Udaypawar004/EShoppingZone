package com.userservice.feign;

import com.userservice.dto.NotificationDetailsDTO;
import com.userservice.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "NOTIFICATIONSERVICE")
public interface NotificationClient {

    @PostMapping("/notification/register")
    String sendRegistrationEmail(@RequestBody User emailId, @RequestParam String token);

    @PostMapping("/notification/sendMail")
    String sendEmailResponse(@RequestBody NotificationDetailsDTO details);
}

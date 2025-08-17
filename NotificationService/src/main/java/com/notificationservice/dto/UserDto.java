package com.notificationservice.dto;

import lombok.Data;

@Data
public class UserDto {
    private int userId;

    private String fullName;

    private String image;

    private String emailId;

    private String mobileNumber;

    private String about;

    private String gender;
}

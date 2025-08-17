package com.userservice.controller;

import com.userservice.dto.ForgetPassDTO;
import com.userservice.dto.JwtRequest;
import com.userservice.dto.JwtResponse;
import com.userservice.dto.PasswordDTO;
import com.userservice.entity.User;
import com.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Operation(summary = "User Login", description = "Authenticate a user and return a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
    	System.out.println(request.toString());
        return userService.login(request);
    }

    @Operation(summary = "Register a new user", description = "Create a new account in the system.")
    @PostMapping(value = "/register", consumes = "application/json;charset=UTF-8")
    public ResponseEntity<String> createUser(@Valid @RequestBody @Validated User user) {
        return userService.createUser(user);
    }
    @Operation(summary = "Update user password", description = "Update the password of an existing user by their ID.")
    @PutMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(HttpServletRequest request, @Valid @RequestBody PasswordDTO password) {
        return userService.updatePassword(request, password.getPassword());
    }
    
    @PostMapping("/forgetPassword")
    public ResponseEntity<String> forgetPassword(@Valid @RequestBody ForgetPassDTO forgetPassDTO) {
    	return userService.forgetPassword(forgetPassDTO.getEmailId());
    }
    
}
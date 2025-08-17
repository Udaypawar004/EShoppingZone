package com.userservice.service;

import com.userservice.dto.*;
import com.userservice.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<String> createUser(User user);

    ResponseEntity<List<User>> getAllUsers();

    ResponseEntity<String> deleteUser(int userId);

    ResponseEntity<String> updateUser(int userId, User user);

    ResponseEntity<User> getUserById(int userId);

    User getUserByEmailId(String emailId);

    ResponseEntity<JwtResponse> login(JwtRequest request);

    ResponseEntity<String> updatePassword(HttpServletRequest request, String password);

    ResponseEntity<List<CartDTO>> getCart(int customerId);

    ResponseEntity<List<OrderDTO>> getOrders(int customerId);


    ResponseEntity<AddressDTO> getAddressByUserId(int userId);

    ResponseEntity<User> viewProfile(HttpServletRequest request);

	ResponseEntity<String> forgetPassword(String emailId);
}

package com.userservice.service;

import com.userservice.entity.User;
import com.userservice.exception.UserNotFoundException;
import com.userservice.feign.NotificationClient;
import com.userservice.filter.JwtAuthenticationFilter;
import com.userservice.repository.UserRepository;

import com.userservice.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @Mock
    NotificationClient notificationClient;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private HttpServletRequest request;

    // createUser method
    @Test
    void shouldReturnBadRequestWhenUserAlreadyExists() {
        User user = new User();
        user.setEmailId("udaypawar4@gmail.com");
        Mockito.when(userRepository.findByEmailId(user.getEmailId())).thenReturn(user);
        ResponseEntity<String> actual = userService.createUser(user);
        ResponseEntity<String> expected = new ResponseEntity<>("User already exists with this email id", HttpStatus.BAD_REQUEST);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnCreatedWhenEmailSentAndUserSavedSuccessfully() throws Exception {
        User user = new User();
        user.setEmailId("new@example.com");
        user.setUserId(123);

        Mockito.when(userRepository.findByEmailId(user.getEmailId())).thenReturn(null);
        Mockito.when(notificationClient.sendRegistrationEmail(Mockito.eq(user), Mockito.anyString())).thenReturn("Email sent");
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");

        ResponseEntity<String> response = userService.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("User Created With UserId"));
        Mockito.verify(userRepository).save(user);
    }

    // getAllUser method
    @Test
    void shouldReturnAllUsers() {
        List<User> mockUsers = List.of(
                new User(),
                new User()
        );

        Mockito.when(userRepository.findAll()).thenReturn(mockUsers);

        ResponseEntity<List<User>> response = userService.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUsers.size(), response.getBody().size());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<User>> response = userService.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    // delete user
    @Test
    void shouldDeleteUserSuccessfully() {
        int userId = 123;

        // No exception should be thrown on delete
        Mockito.doNothing().when(userRepository).deleteById(userId);

        ResponseEntity<String> response = userService.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
        Mockito.verify(userRepository).deleteById(userId);
    }

    @Test
    void shouldThrowExceptionIfDeleteFails() {
        int userId = 999;

        Mockito.doThrow(new RuntimeException("Deletion failed")).when(userRepository).deleteById(userId);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
    }

    // Update User
    @Test
    void shouldUpdateUserSuccessfully() {
        int userId = 1;
        User incomingUser = new User();
        incomingUser.setFullName("Updated Name");
        incomingUser.setEmailId("updated@example.com");
        incomingUser.setMobileNumber("9090900909");
        incomingUser.setAbout("Updated about");
        incomingUser.setDateOfBirth(new Date());

        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setFullName("Old Name");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);

        ResponseEntity<String> response = userService.updateUser(userId, incomingUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User updated successfully", response.getBody());
        Mockito.verify(userRepository).save(existingUser);

        assertEquals("Updated Name", existingUser.getFullName());
        assertEquals("updated@example.com", existingUser.getEmailId());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        int userId = 42;
        User incomingUser = new User();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(userId, incomingUser)
        );

        assertEquals("User not found", exception.getMessage());
    }

    // getUserById method
    @Test
    void shouldReturnUserByIdSuccessfully() {
        int userId = 101;
        User user = new User();
        user.setUserId(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userService.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserIdMissing() {
        int userId = 404;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(userId)
        );

        assertEquals("User not found", exception.getMessage());
    }

    // getUserByEmailId method
    @BeforeEach
    void setupSecurityContext() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.lenient().when(authentication.getName()).thenReturn("authenticatedUser");
        Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);


        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldReturnUserWhenEmailIsValid() {
        String email = "valid@example.com";
        User mockUser = new User();
        mockUser.setEmailId(email);

        Mockito.when(userRepository.findByEmailId(email)).thenReturn(mockUser);

        User result = userService.getUserByEmailId(email);

        assertNotNull(result);
        assertEquals(email, result.getEmailId());
    }

    @Test
    void shouldReturnNullWhenEmailIsInvalid() {
        String email = "invalid@example.com";
        Mockito.when(userRepository.findByEmailId(email)).thenReturn(null);

        User result = userService.getUserByEmailId(email);

        assertNull(result);
    }

    // viewProfile method
    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() {
        Mockito.when(jwtAuthenticationFilter.getTokenFromHeader(request)).thenReturn(null);

        ResponseEntity<User> response = userService.viewProfile(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void shouldReturnUserProfileSuccessfully() {
        String token = "valid.jwt.token";
        String email = "test@example.com";
        User user = new User();
        user.setEmailId(email);

        Mockito.when(jwtAuthenticationFilter.getTokenFromHeader(request)).thenReturn(token);
        Mockito.when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(email);
        Mockito.when(userRepository.findByEmailId(email)).thenReturn(user);

        // Optionally spy to stub getUserByEmailId if it's an internal call
        ResponseEntity<User> response = userService.viewProfile(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void shouldReturnOkWithNullUserWhenEmailNotFound() {
        String token = "token123";
        String email = "missing@example.com";

        Mockito.when(jwtAuthenticationFilter.getTokenFromHeader(request)).thenReturn(token);
        Mockito.when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(email);
        Mockito.when(userRepository.findByEmailId(email)).thenReturn(null);

        ResponseEntity<User> response = userService.viewProfile(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }
}
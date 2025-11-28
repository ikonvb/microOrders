package org.bkv.orders.controllers;

import org.bkv.orders.dto.requests.LoginUserRequest;
import org.bkv.orders.dto.requests.RegisterUserRequest;
import org.bkv.orders.dto.responses.LoginUserResponse;
import org.bkv.orders.dto.responses.RefreshResponse;
import org.bkv.orders.dto.responses.RegisterUserResponse;
import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.models.LoginResult;
import org.bkv.orders.models.UserDto;
import org.bkv.orders.services.impls.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void register_success() {
        RegisterUserRequest request = new RegisterUserRequest("testUser", "email", "password", "user");
        UserEntity savedUser = new UserEntity();
        savedUser.setUserId(1L);

        when(userService.saveAndGetUserEntity(request)).thenReturn(savedUser);

        ResponseEntity<RegisterUserResponse> response = authController.register(request);

        assertTrue(response.getBody().status());
        verify(userService, times(1)).saveAndGetUserEntity(request);
    }

    @Test
    void register_failure() {
        RegisterUserRequest request = new RegisterUserRequest("testUser", "email", "password", "user");

        when(userService.saveAndGetUserEntity(request)).thenReturn(null);

        ResponseEntity<RegisterUserResponse> response = authController.register(request);

        assertFalse(response.getBody().status());
        verify(userService, times(1)).saveAndGetUserEntity(request);
    }


    @Test
    void login_success() {

        LoginUserRequest request = new LoginUserRequest("user", "pass");
        UserDto user = new UserDto(1L, "", "", "");

        LoginResult loginResult = new LoginResult(user, "accessToken123", "refreshToken123");

        when(userService.login("user", "pass")).thenReturn(loginResult);

        ResponseEntity<LoginUserResponse> response = authController.login(request);

        assertEquals(user, response.getBody().user());
        assertEquals("accessToken123", response.getBody().accessToken());
        assertTrue(response.getBody().status());

        String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookieHeader);
        assertTrue(setCookieHeader.contains("refreshToken=refreshToken123"));
    }

    @Test
    void login_badCredentials() {
        LoginUserRequest request = new LoginUserRequest("user", "wrongpass");

        when(userService.login("user", "wrongpass")).thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<LoginUserResponse> response = authController.login(request);

        assertNull(response.getBody().user());
        assertEquals("", response.getBody().accessToken());
        assertFalse(response.getBody().status());
    }

    @Test
    void refresh_success() {
        String oldToken = "oldToken";
        String newToken = "newToken";

        when(userService.checkRefreshToken(oldToken)).thenReturn(true);
        when(userService.createRefreshToken(oldToken)).thenReturn(newToken);

        ResponseEntity<RefreshResponse> response = authController.refresh(oldToken);

        assertTrue(response.getBody().status());
        assertEquals(newToken, response.getBody().token());
    }

    @Test
    void refresh_missingToken() {
        ResponseEntity<RefreshResponse> response = authController.refresh(null);

        assertFalse(response.getBody().status());
        assertEquals("", response.getBody().token());
    }

    @Test
    void refresh_invalidToken() {
        String invalidToken = "invalidToken";

        when(userService.checkRefreshToken(invalidToken)).thenReturn(false);

        ResponseEntity<RefreshResponse> response = authController.refresh(invalidToken);

        assertFalse(response.getBody().status());
        assertEquals("", response.getBody().token());
    }
}
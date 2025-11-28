package org.bkv.orders.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bkv.orders.dto.requests.LoginUserRequest;
import org.bkv.orders.dto.requests.RegisterUserRequest;
import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.security.JwtUtil;
import org.bkv.orders.services.impls.UserService;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private AuthenticationManager authManager;

    // ----------------------------------------------------------
    // REGISTER
    // ----------------------------------------------------------
    @Test
    void registerSuccess() throws Exception {

        RegisterUserRequest req = new RegisterUserRequest("newUser", "pass");

        Mockito.when(userService.findByUserName("newUser"))
                .thenReturn(Optional.empty());

        Mockito.when(userService.saveUser(any(UserEntity.class)))
                .thenReturn(new UserEntity());

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void registerUserAlreadyExists() throws Exception {

        RegisterUserRequest req = new RegisterUserRequest("user", "pass");

        Mockito.when(userService.findByUserName("user"))
                .thenReturn(Optional.of(new UserEntity()));

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }


    // ----------------------------------------------------------
    // LOGIN
    // ----------------------------------------------------------
    @Test
    void loginSuccess() throws Exception {

        LoginUserRequest req = new LoginUserRequest("user", "pass");

        // mock authentication
        User springUser = new User("user", "encoded", java.util.List.of());
        Mockito.when(authManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(springUser, null, springUser.getAuthorities()));

        Mockito.when(userService.findByUserName("user"))
                .thenReturn(Optional.of(new UserEntity()));

        Mockito.when(jwtUtil.generateAccessToken("user"))
                .thenReturn("ACCESS_TOKEN");

        Mockito.when(jwtUtil.generateRefreshToken("user"))
                .thenReturn("REFRESH_TOKEN");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").value("ACCESS_TOKEN"));
    }

    @Test
    void loginBadCredentials() throws Exception {

        LoginUserRequest req = new LoginUserRequest("u", "p");

        Mockito.when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }


    // ----------------------------------------------------------
    // REFRESH
    // ----------------------------------------------------------
    @Test
    void refreshSuccess() throws Exception {

        Mockito.when(jwtUtil.isRefreshTokenValid("REFRESH")).thenReturn(true);
        Mockito.when(jwtUtil.extractUserName("REFRESH")).thenReturn("user");
        Mockito.when(jwtUtil.generateAccessToken("user")).thenReturn("NEW_ACCESS");

        mvc.perform(post("/api/auth/refresh")
                        .cookie(new javax.servlet.http.Cookie("refreshToken", "REFRESH"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").value("NEW_ACCESS"));
    }

    @Test
    void refreshInvalidToken() throws Exception {

        Mockito.when(jwtUtil.isRefreshTokenValid("BAD")).thenReturn(false);

        mvc.perform(post("/api/auth/refresh")
                        .cookie(new javax.servlet.http.Cookie("refreshToken", "BAD")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void refreshNoCookie() throws Exception {

        mvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
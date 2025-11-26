package org.bkv.orders.controllers;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.bkv.orders.dto.requests.LoginUserRequest;
import org.bkv.orders.dto.requests.RegisterUserRequest;
import org.bkv.orders.dto.responses.LoginUserResponse;
import org.bkv.orders.dto.responses.RefreshResponse;
import org.bkv.orders.dto.responses.RegisterUserResponse;
import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.mappers.Mappers;
import org.bkv.orders.models.UserDto;
import org.bkv.orders.security.JwtUtil;
import org.bkv.orders.services.impls.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController()
@RequestMapping(path = "/api/auth", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AuthController {


    private final Integer COOKIE_MAX_AGE = 7 * 24 * 3600;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(path = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<@NonNull RegisterUserResponse> register(@Valid @NotNull @RequestBody RegisterUserRequest registerUser) {

        logger.debug("Registering user: {}", registerUser);

        if (userService.findByUserName(registerUser.userName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new RegisterUserResponse(false));
        }

        UserEntity user = Mappers.toUserEntity(registerUser, passwordEncoder);
        UserEntity savedUser = userService.saveUser(user);

        if (savedUser == null) {
            logger.debug("Registering user: {}, completed", registerUser);
            return ResponseEntity.ok(new RegisterUserResponse(false));
        } else {
            logger.debug("Registering user: {}, failed", registerUser);
            return ResponseEntity.ok(new RegisterUserResponse(true));
        }
    }

    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<@NonNull LoginUserResponse> login(@Valid @NotNull @RequestBody LoginUserRequest request) {

        try {

            logger.debug("Logging in user: {}", request);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.userName(), request.password())
            );

            UserDto userDto = userService.findByUserName(request.userName()).map(Mappers::toUserDto).orElse(null);

            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String accessToken = null;
            String refreshToken = null;

            if (userDetails != null) {
                accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
                refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
            }

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)       // защищает от XSS
                    .secure(true)         // работает только через HTTPS
                    .path("/api/auth")    // отправляется только на auth
                    .maxAge(COOKIE_MAX_AGE) // 7 days
                    .sameSite("Strict")   // защищает от CSRF
                    .build();

            LoginUserResponse response = new LoginUserResponse(userDto, accessToken, true);

            logger.debug("Logging in user: {}, completed", request);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(response);

        } catch (BadCredentialsException e) {

            logger.debug("Logging in user: {}, failed", request);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginUserResponse(null, "", false));
        }

    }

    @PostMapping(path = "/refresh", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<@NonNull RefreshResponse> refresh(
            @CookieValue(name = "refreshToken")
            String refreshToken
    ) {

        logger.debug("Refreshing accessToken: {}", refreshToken);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RefreshResponse("", false));
        }

        if (!jwtUtil.isRefreshToken(refreshToken)) {
            logger.debug("Refreshing accessToken: {}, failed", refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RefreshResponse("", false));
        }

        String username = jwtUtil.extractUserName(refreshToken);
        String newToken = jwtUtil.generateAccessToken(username);

        logger.debug("Refreshing accessToken: {}, completed", newToken);

        return ResponseEntity.ok(new RefreshResponse(newToken, true));
    }
}

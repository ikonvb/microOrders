package org.bkv.orders.controllers;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bkv.orders.dto.requests.LoginUserRequest;
import org.bkv.orders.dto.requests.RegisterUserRequest;
import org.bkv.orders.dto.responses.LoginUserResponse;
import org.bkv.orders.dto.responses.RefreshResponse;
import org.bkv.orders.dto.responses.RegisterUserResponse;
import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.models.LoginResult;
import org.bkv.orders.services.impls.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController()
@RequestMapping(path = "/api/auth", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AuthController {

    private final Integer COOKIE_MAX_AGE = 7 * 24 * 3600;

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping(path = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<@NonNull RegisterUserResponse> register(@Valid @NotNull @RequestBody RegisterUserRequest registerUser) {

        logger.debug("Registering user: {}", registerUser);

        UserEntity savedUser = userService.saveAndGetUserEntity(registerUser);

        if (savedUser == null) {
            logger.debug("Registering user: {}, completed", registerUser);
            return ResponseEntity.ok(new RegisterUserResponse(false));
        } else {
            logger.debug("Registering user: {}, failed", registerUser);
            return ResponseEntity.ok(new RegisterUserResponse(true));
        }
    }

    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<@NonNull LoginUserResponse> login(
            @Valid @NotNull @RequestBody LoginUserRequest request) {

        try {

            logger.info("Logging called 1");

            LoginResult result = userService.login(request.userName(), request.password());

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.refreshToken())
                    .httpOnly(true)       // защищает от XSS
                    .secure(true)         // работает только через HTTPS
                    .path("/api/auth")    // отправляется только на auth
                    .maxAge(COOKIE_MAX_AGE) // 7 days
                    .sameSite("Strict")   // защищает от CSRF
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(new LoginUserResponse(result.user(), result.accessToken(), true));

        } catch (BadCredentialsException e) {

            logger.debug("Logging in user: {}, failed", request);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginUserResponse(null, "", false));
        }

    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<@NonNull RefreshResponse> refresh(
            @CookieValue(name = "refreshToken", required = false)
            String refreshToken
    ) {

        logger.info("Refresh called with refreshToken = " + refreshToken);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RefreshResponse("", false));
        }

        if (!userService.checkRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RefreshResponse("", false));
        }

        String newToken = userService.createRefreshToken(refreshToken);

        return ResponseEntity.ok(new RefreshResponse(newToken, true));
    }
}

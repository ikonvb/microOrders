package org.bkv.orders.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Component
public class RefreshTokenCookieFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(RefreshTokenCookieFilter.class);
    private final Integer COOKIE_MAX_AGE = 7 * 24 * 3600;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        logger.info("doFilterInternal called");

        String refreshToken = (String) request.getAttribute("refreshToken");

        logger.info("doFilterInternal called refreshToken = " + refreshToken);

        if (refreshToken != null) {
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)       // защищает от XSS
                    .secure(false)         // работает только через HTTPS
                    .path("/")    // отправляется только на auth
                    .maxAge(COOKIE_MAX_AGE) // 7 days
                    .sameSite("Strict")   // защищает от CSRF
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }

        filterChain.doFilter(request, response);
    }
}

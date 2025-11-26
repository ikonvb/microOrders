package org.bkv.orders.dto.requests;

public record RegisterUserRequest(
        String userName,
        String email,
        String password,
        String role
) {
}

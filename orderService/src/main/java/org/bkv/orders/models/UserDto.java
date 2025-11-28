package org.bkv.orders.models;

public record UserDto(
        Long userId,
        String userName,
        String email,
        String role
) {
}